package com.loanguard.dto;

import com.loanguard.model.AuditLog;

public record AuditLogResponse(
        Long id, String userName, String action, String entityType,
        Long entityId, String details, String ipAddress, String createdAt
) {
    public static AuditLogResponse from(AuditLog a) {
        return new AuditLogResponse(
                a.getId(),
                a.getUser() != null ? a.getUser().getFullName() : "System",
                a.getAction(), a.getEntityType(), a.getEntityId(),
                a.getDetails(), a.getIpAddress(), a.getCreatedAt().toString()
        );
    }
}