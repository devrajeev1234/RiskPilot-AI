package com.loanguard.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_comments")
public class LoanComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private LoanApplication loan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 2000)
    private String comment;

    @Column(name = "is_internal", nullable = false)
    private Boolean isInternal = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId()                        { return id; }
    public void setId(Long v)                  { this.id = v; }
    public LoanApplication getLoan()           { return loan; }
    public void setLoan(LoanApplication v)     { this.loan = v; }
    public User getUser()                      { return user; }
    public void setUser(User v)                { this.user = v; }
    public String getComment()                 { return comment; }
    public void setComment(String v)           { this.comment = v; }
    public Boolean getIsInternal()             { return isInternal; }
    public void setIsInternal(Boolean v)       { this.isInternal = v; }
    public LocalDateTime getCreatedAt()        { return createdAt; }
    public void setCreatedAt(LocalDateTime v)  { this.createdAt = v; }
}