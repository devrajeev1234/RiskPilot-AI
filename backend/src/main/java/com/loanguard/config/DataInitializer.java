package com.loanguard.config;

import com.loanguard.model.User;
import com.loanguard.model.UserRole;
import com.loanguard.repository.LoanApplicationRepository;
import com.loanguard.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initAdminUser(UserRepository userRepository, LoanApplicationRepository loanApplicationRepository) {
        return args -> {
            String adminEmail = "admin@loanguard.local";
            if (userRepository.existsByEmail(adminEmail)) {
                User existing = userRepository.findByEmail(adminEmail).orElse(null);
                if (existing != null && existing.getRole() != UserRole.ADMIN) {
                    existing.setRole(UserRole.ADMIN);
                    userRepository.save(existing);
                }
            } else {
                User admin = new User();
                admin.setFullName("RiskPilot AI Admin");
                admin.setEmail(adminEmail);
                admin.setPassword(BCrypt.hashpw("Admin@123", BCrypt.gensalt(12)));
                admin.setRole(UserRole.ADMIN);
                admin.setIsActive(true);
                userRepository.save(admin);
                System.out.println("[DataInitializer] Created default admin user: " + adminEmail + " / Admin@123");
            }

            var loans = loanApplicationRepository.findAll();
            boolean updated = false;
            for (var loan : loans) {
                if (loan.getUser() == null) {
                    continue;
                }
                var loanUser = userRepository.findById(loan.getUser().getId()).orElse(null);
                if (loanUser == null) {
                    continue;
                }
                if (loan.getApplicantName() == null || loan.getApplicantName().isBlank()) {
                    loan.setApplicantName(loanUser.getFullName());
                    updated = true;
                }
                if (loan.getApplicantEmail() == null || loan.getApplicantEmail().isBlank()) {
                    loan.setApplicantEmail(loanUser.getEmail());
                    updated = true;
                }
            }
            if (updated) {
                loanApplicationRepository.saveAll(loans);
            }
        };
    }
}