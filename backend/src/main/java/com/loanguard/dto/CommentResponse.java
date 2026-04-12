package com.loanguard.dto;

import com.loanguard.model.LoanComment;

public record CommentResponse(
        Long id, Long loanId, String userName, String userRole,
        String comment, Boolean isInternal, String createdAt
) {
    public static CommentResponse from(LoanComment c) {
        return new CommentResponse(
                c.getId(), c.getLoan().getId(),
                c.getUser().getFullName(), c.getUser().getRole().name(),
                c.getComment(), c.getIsInternal(), c.getCreatedAt().toString()
        );
    }
}