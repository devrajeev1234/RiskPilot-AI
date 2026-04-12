package com.loanguard.dto;

import com.loanguard.model.Document;

public record DocumentResponse(
        Long id, Long loanId, String fileName, String fileType,
        Long fileSize, String docType, Boolean verified,
        String verifiedBy, String verifiedAt, String createdAt
) {
    public static DocumentResponse from(Document d) {
        return new DocumentResponse(
                d.getId(), d.getLoan().getId(), d.getFileName(),
                d.getFileType(), d.getFileSize(), d.getDocType(),
                d.getVerified(),
                d.getVerifiedBy() != null ? d.getVerifiedBy().getFullName() : null,
                d.getVerifiedAt() != null ? d.getVerifiedAt().toString() : null,
                d.getCreatedAt().toString()
        );
    }
}