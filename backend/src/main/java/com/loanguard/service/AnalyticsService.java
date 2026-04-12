package com.loanguard.service;

import com.loanguard.dto.AnalyticsResponse;
import com.loanguard.model.LoanOutcome;
import com.loanguard.model.LoanStatus;
import com.loanguard.repository.LoanApplicationRepository;
import com.loanguard.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnalyticsService {

    private final LoanApplicationRepository loanRepo;
    private final UserRepository userRepo;

    public AnalyticsService(LoanApplicationRepository loanRepo, UserRepository userRepo) {
        this.loanRepo = loanRepo;
        this.userRepo = userRepo;
    }

    @Transactional(readOnly = true)
    public AnalyticsResponse getSystemAnalytics() {
        long total       = loanRepo.count();
        long approved    = loanRepo.countByStatus(LoanStatus.APPROVED);
        long rejected    = loanRepo.countByStatus(LoanStatus.REJECTED);
        long review      = loanRepo.countByStatus(LoanStatus.UNDER_REVIEW);
        long repaid      = loanRepo.countByActualOutcome(LoanOutcome.REPAID);
        long defaulted   = loanRepo.countByActualOutcome(LoanOutcome.DEFAULTED);
        long pending     = loanRepo.countByActualOutcome(LoanOutcome.PENDING);
        long users       = userRepo.count();

        Double avgProb   = loanRepo.avgDefaultProbability();
        Double avgRate   = loanRepo.avgInterestRate();
        Double totalAmt  = loanRepo.totalApprovedAmount();
        Double avgReward = loanRepo.avgReward();

        double approvalRate  = total > 0 ? (double) approved / total * 100 : 0;
        double defaultRate   = (repaid + defaulted) > 0 ? (double) defaulted / (repaid + defaulted) * 100 : 0;
        double repaymentRate = (repaid + defaulted) > 0 ? (double) repaid / (repaid + defaulted) * 100 : 0;

        return new AnalyticsResponse(
                total, approved, rejected, review,
                repaid, defaulted, pending, users,
                avgProb, avgRate, totalAmt, avgReward,
                approvalRate, defaultRate, repaymentRate
        );
    }
}
