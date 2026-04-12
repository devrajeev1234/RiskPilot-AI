package com.loanguard.service;

import com.loanguard.dto.CommentRequest;
import com.loanguard.dto.CommentResponse;
import com.loanguard.exception.ApiException;
import com.loanguard.model.LoanApplication;
import com.loanguard.model.LoanComment;
import com.loanguard.model.User;
import com.loanguard.model.UserRole;
import com.loanguard.repository.LoanApplicationRepository;
import com.loanguard.repository.LoanCommentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

    private final LoanCommentRepository commentRepo;
    private final LoanApplicationRepository loanRepo;

    public CommentService(LoanCommentRepository commentRepo, LoanApplicationRepository loanRepo) {
        this.commentRepo = commentRepo;
        this.loanRepo    = loanRepo;
    }

    @Transactional
    public CommentResponse addComment(User user, CommentRequest req) {
        LoanApplication loan = loanRepo.findById(req.loanId())
                .orElseThrow(() -> new ApiException("Loan not found", HttpStatus.NOT_FOUND));

        boolean isStaff = user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.LOAN_OFFICER;
        if (!isStaff && !loan.getUser().getId().equals(user.getId())) {
            throw new ApiException("Not authorized", HttpStatus.FORBIDDEN);
        }

        LoanComment comment = new LoanComment();
        comment.setLoan(loan);
        comment.setUser(user);
        comment.setComment(req.comment());
        comment.setIsInternal(req.isInternal() != null && req.isInternal() && isStaff);

        return CommentResponse.from(commentRepo.save(comment));
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(User user, Long loanId) {
        boolean isStaff = user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.LOAN_OFFICER;
        if (isStaff) {
            return commentRepo.findByLoanIdOrderByCreatedAtAsc(loanId).stream()
                    .map(CommentResponse::from).toList();
        }
        return commentRepo.findByLoanIdAndIsInternalFalseOrderByCreatedAtAsc(loanId).stream()
                .map(CommentResponse::from).toList();
    }
}