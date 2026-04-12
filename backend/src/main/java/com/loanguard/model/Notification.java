package com.loanguard.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String message;

    @Column(nullable = false)
    private String type;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    private String link;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId()                        { return id; }
    public void setId(Long v)                  { this.id = v; }
    public User getUser()                      { return user; }
    public void setUser(User v)                { this.user = v; }
    public String getTitle()                   { return title; }
    public void setTitle(String v)             { this.title = v; }
    public String getMessage()                 { return message; }
    public void setMessage(String v)           { this.message = v; }
    public String getType()                    { return type; }
    public void setType(String v)              { this.type = v; }
    public Boolean getIsRead()                 { return isRead; }
    public void setIsRead(Boolean v)           { this.isRead = v; }
    public String getLink()                    { return link; }
    public void setLink(String v)              { this.link = v; }
    public LocalDateTime getCreatedAt()        { return createdAt; }
    public void setCreatedAt(LocalDateTime v)  { this.createdAt = v; }
}