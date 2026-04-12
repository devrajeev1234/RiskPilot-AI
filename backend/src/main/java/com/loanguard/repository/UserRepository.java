package com.loanguard.repository;

import com.loanguard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByAuthProviderAndProviderSubject(String authProvider, String providerSubject);
    boolean existsByEmail(String email);
}