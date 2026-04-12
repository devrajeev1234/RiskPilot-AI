package com.loanguard.repository;

import com.loanguard.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByLoanIdOrderByCreatedAtDesc(Long loanId);
    List<Document> findByLoanId(Long loanId);
    long countByLoanIdAndVerifiedFalse(Long loanId);
}