package com.loanguard.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private LoanApplication loan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_type", nullable = false)
    private String fileType;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "doc_type", nullable = false)
    private String docType;

    @Column(nullable = false)
    private Boolean verified = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by")
    private User verifiedBy;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId()                          { return id; }
    public void setId(Long v)                    { this.id = v; }
    public LoanApplication getLoan()             { return loan; }
    public void setLoan(LoanApplication v)       { this.loan = v; }
    public User getUser()                        { return user; }
    public void setUser(User v)                  { this.user = v; }
    public String getFileName()                  { return fileName; }
    public void setFileName(String v)            { this.fileName = v; }
    public String getFileType()                  { return fileType; }
    public void setFileType(String v)            { this.fileType = v; }
    public Long getFileSize()                    { return fileSize; }
    public void setFileSize(Long v)              { this.fileSize = v; }
    public String getDocType()                   { return docType; }
    public void setDocType(String v)             { this.docType = v; }
    public Boolean getVerified()                 { return verified; }
    public void setVerified(Boolean v)           { this.verified = v; }
    public User getVerifiedBy()                  { return verifiedBy; }
    public void setVerifiedBy(User v)            { this.verifiedBy = v; }
    public LocalDateTime getVerifiedAt()         { return verifiedAt; }
    public void setVerifiedAt(LocalDateTime v)   { this.verifiedAt = v; }
    public LocalDateTime getCreatedAt()          { return createdAt; }
    public void setCreatedAt(LocalDateTime v)    { this.createdAt = v; }
}