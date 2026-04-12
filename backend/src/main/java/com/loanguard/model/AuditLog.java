package com.loanguard.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String action;

    @Column(name = "entity_type", nullable = false)
    private String entityType;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(length = 2000)
    private String details;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId()                        { return id; }
    public void setId(Long v)                  { this.id = v; }
    public User getUser()                      { return user; }
    public void setUser(User v)                { this.user = v; }
    public String getAction()                  { return action; }
    public void setAction(String v)            { this.action = v; }
    public String getEntityType()              { return entityType; }
    public void setEntityType(String v)        { this.entityType = v; }
    public Long getEntityId()                  { return entityId; }
    public void setEntityId(Long v)            { this.entityId = v; }
    public String getDetails()                 { return details; }
    public void setDetails(String v)           { this.details = v; }
    public String getIpAddress()               { return ipAddress; }
    public void setIpAddress(String v)         { this.ipAddress = v; }
    public LocalDateTime getCreatedAt()        { return createdAt; }
    public void setCreatedAt(LocalDateTime v)  { this.createdAt = v; }
}