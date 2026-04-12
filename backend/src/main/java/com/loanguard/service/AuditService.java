package com.loanguard.service;

import com.loanguard.dto.AuditLogResponse;
import com.loanguard.model.AuditLog;
import com.loanguard.model.User;
import com.loanguard.repository.AuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {

    private final AuditLogRepository auditRepo;

    public AuditService(AuditLogRepository auditRepo) {
        this.auditRepo = auditRepo;
    }

    @Transactional
    public void log(User user, String action, String entityType, Long entityId, String details, String ip) {
        AuditLog entry = new AuditLog();
        entry.setUser(user);
        entry.setAction(action);
        entry.setEntityType(entityType);
        entry.setEntityId(entityId);
        entry.setDetails(details);
        entry.setIpAddress(ip);
        auditRepo.save(entry);
    }

    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getAll(int page, int size) {
        return auditRepo.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size))
                .map(AuditLogResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getByAction(String action, int page, int size) {
        return auditRepo.findByActionOrderByCreatedAtDesc(action, PageRequest.of(page, size))
                .map(AuditLogResponse::from);
    }
}