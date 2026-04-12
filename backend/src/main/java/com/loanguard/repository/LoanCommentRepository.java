package com.loanguard.repository;

import com.loanguard.model.LoanComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanCommentRepository extends JpaRepository<LoanComment, Long> {
    List<LoanComment> findByLoanIdOrderByCreatedAtAsc(Long loanId);
    List<LoanComment> findByLoanIdAndIsInternalFalseOrderByCreatedAtAsc(Long loanId);
}