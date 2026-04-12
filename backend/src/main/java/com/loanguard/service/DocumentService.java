package com.loanguard.service;

import com.loanguard.dto.DocumentResponse;
import com.loanguard.exception.ApiException;
import com.loanguard.model.Document;
import com.loanguard.model.LoanApplication;
import com.loanguard.model.User;
import com.loanguard.repository.DocumentRepository;
import com.loanguard.repository.LoanApplicationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DocumentService {

    private final DocumentRepository docRepo;
    private final LoanApplicationRepository loanRepo;

    public DocumentService(DocumentRepository docRepo, LoanApplicationRepository loanRepo) {
        this.docRepo  = docRepo;
        this.loanRepo = loanRepo;
    }

    @Transactional
    public DocumentResponse uploadMeta(User user, Long loanId, String fileName,
                                       String fileType, Long fileSize, String docType) {
        LoanApplication loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new ApiException("Loan not found", HttpStatus.NOT_FOUND));

        if (!loan.getUser().getId().equals(user.getId())) {
            throw new ApiException("Not authorized", HttpStatus.FORBIDDEN);
        }

        Document doc = new Document();
        doc.setLoan(loan);
        doc.setUser(user);
        doc.setFileName(fileName);
        doc.setFileType(fileType);
        doc.setFileSize(fileSize);
        doc.setDocType(docType);

        return DocumentResponse.from(docRepo.save(doc));
    }

    @Transactional
    public DocumentResponse verify(User admin, Long docId) {
        Document doc = docRepo.findById(docId)
                .orElseThrow(() -> new ApiException("Document not found", HttpStatus.NOT_FOUND));

        doc.setVerified(true);
        doc.setVerifiedBy(admin);
        doc.setVerifiedAt(LocalDateTime.now());

        return DocumentResponse.from(docRepo.save(doc));
    }

    @Transactional(readOnly = true)
    public List<DocumentResponse> getByLoan(Long loanId) {
        return docRepo.findByLoanIdOrderByCreatedAtDesc(loanId).stream()
                .map(DocumentResponse::from).toList();
    }
}
