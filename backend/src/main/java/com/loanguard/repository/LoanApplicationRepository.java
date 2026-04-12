package com.loanguard.repository;

import com.loanguard.model.LoanApplication;
import com.loanguard.model.LoanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {
    List<LoanApplication> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<LoanApplication> findByApplicationRef(String ref);
    Page<LoanApplication> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<LoanApplication> findByStatusOrderByCreatedAtDesc(LoanStatus status, Pageable pageable);
    long countByStatus(LoanStatus status);
    long countByActualOutcome(com.loanguard.model.LoanOutcome outcome);

    @Query("SELECT AVG(l.defaultProbability) FROM LoanApplication l WHERE l.defaultProbability IS NOT NULL")
    Double avgDefaultProbability();

    @Query("SELECT AVG(l.offeredInterestRate) FROM LoanApplication l WHERE l.offeredInterestRate IS NOT NULL AND l.offeredInterestRate > 0")
    Double avgInterestRate();

    @Query("SELECT SUM(l.loanAmount) FROM LoanApplication l WHERE l.status = 'APPROVED'")
    Double totalApprovedAmount();

    @Query("SELECT AVG(l.rewardReceived) FROM LoanApplication l WHERE l.rewardReceived IS NOT NULL")
    Double avgReward();

    List<LoanApplication> findByNeedsAdminReviewTrueAndStatusOrderByCreatedAtDesc(LoanStatus status);

    long countByNeedsAdminReviewTrueAndStatus(LoanStatus status);
}