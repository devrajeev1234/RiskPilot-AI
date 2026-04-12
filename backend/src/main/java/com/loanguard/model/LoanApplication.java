package com.loanguard.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_application")
public class LoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "applicant_name", nullable = false)
    private String applicantName;

    @Column(name = "applicant_email", nullable = false)
    private String applicantEmail;

    @Column(name = "application_ref", nullable = false, unique = true)
    private String applicationRef;

    @Column(name = "annual_income", nullable = false)
    private Double annualIncome;

    @Column(name = "loan_amount", nullable = false)
    private Double loanAmount;

    @Column(name = "existing_debt", nullable = false)
    private Double existingDebt;

    @Column(name = "employment_years", nullable = false)
    private Integer employmentYears;

    @Column(name = "loan_purpose")
    private String loanPurpose;

    @Column(name = "loan_term_months")
    private Integer loanTermMonths = 36;

    // ── RL Agent Fields ──

    @Column(name = "rl_action")
    private String rlAction;

    @Column(name = "offered_interest_rate")
    private Double offeredInterestRate;

    @Column(name = "default_probability")
    private Double defaultProbability;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level")
    private RiskLevel riskLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private LoanStatus status;

    @Column(name = "advisory_message", length = 1000)
    private String advisoryMessage;

    @Column(name = "rl_state")
    private String rlState;

    @Column(name = "q_values", length = 500)
    private String qValues;

    @Column(name = "confidence_level")
    private String confidenceLevel;

    @Column(name = "needs_admin_review")
    private Boolean needsAdminReview = false;

    @Column(name = "escalation_reason", length = 1000)
    private String escalationReason;

    @Column(name = "admin_note", length = 2000)
    private String adminNote;

    @Column(name = "rl_suggested_action")
    private String rlSuggestedAction;

    @Column(name = "admin_decision")
    private String adminDecision;

    @Column(name = "admin_interest_rate")
    private Double adminInterestRate;

    // ── Outcome Fields ──

    @Enumerated(EnumType.STRING)
    @Column(name = "actual_outcome")
    private LoanOutcome actualOutcome = LoanOutcome.PENDING;

    @Column(name = "reward_received")
    private Double rewardReceived;

    @Column(name = "feedback_given", nullable = false)
    private Boolean feedbackGiven = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "review_notes", length = 1000)
    private String reviewNotes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // ── Getters & Setters ──

    public Long getId()                                    { return id; }
    public void setId(Long id)                             { this.id = id; }

    public User getUser()                                  { return user; }
    public void setUser(User u)                            { this.user = u; }

    public String getApplicantName()                       { return applicantName; }
    public void setApplicantName(String v)                 { this.applicantName = v; }

    public String getApplicantEmail()                      { return applicantEmail; }
    public void setApplicantEmail(String v)                { this.applicantEmail = v; }

    public String getApplicationRef()                      { return applicationRef; }
    public void setApplicationRef(String v)                { this.applicationRef = v; }

    public Double getAnnualIncome()                        { return annualIncome; }
    public void setAnnualIncome(Double v)                  { this.annualIncome = v; }

    public Double getLoanAmount()                          { return loanAmount; }
    public void setLoanAmount(Double v)                    { this.loanAmount = v; }

    public Double getExistingDebt()                        { return existingDebt; }
    public void setExistingDebt(Double v)                  { this.existingDebt = v; }

    public Integer getEmploymentYears()                    { return employmentYears; }
    public void setEmploymentYears(Integer v)              { this.employmentYears = v; }

    public String getLoanPurpose()                         { return loanPurpose; }
    public void setLoanPurpose(String v)                   { this.loanPurpose = v; }

    public Integer getLoanTermMonths()                      { return loanTermMonths; }
    public void setLoanTermMonths(Integer v)               { this.loanTermMonths = v; }

    public String getRlAction()                            { return rlAction; }
    public void setRlAction(String v)                      { this.rlAction = v; }

    public Double getOfferedInterestRate()                 { return offeredInterestRate; }
    public void setOfferedInterestRate(Double v)           { this.offeredInterestRate = v; }

    public Double getDefaultProbability()                  { return defaultProbability; }
    public void setDefaultProbability(Double v)            { this.defaultProbability = v; }

    public RiskLevel getRiskLevel()                        { return riskLevel; }
    public void setRiskLevel(RiskLevel v)                  { this.riskLevel = v; }

    public LoanStatus getStatus()                          { return status; }
    public void setStatus(LoanStatus v)                    { this.status = v; }

    public String getAdvisoryMessage()                     { return advisoryMessage; }
    public void setAdvisoryMessage(String v)               { this.advisoryMessage = v; }

    public String getRlState()                             { return rlState; }
    public void setRlState(String v)                       { this.rlState = v; }

    public String getQValues()                             { return qValues; }
    public void setQValues(String v)                       { this.qValues = v; }

    public String getConfidenceLevel()                     { return confidenceLevel; }
    public void setConfidenceLevel(String v)                { this.confidenceLevel = v; }

    public Boolean getNeedsAdminReview()                    { return needsAdminReview; }
    public void setNeedsAdminReview(Boolean v)              { this.needsAdminReview = v; }

    public String getEscalationReason()                     { return escalationReason; }
    public void setEscalationReason(String v)               { this.escalationReason = v; }

    public String getAdminNote()                            { return adminNote; }
    public void setAdminNote(String v)                      { this.adminNote = v; }

    public String getRlSuggestedAction()                    { return rlSuggestedAction; }
    public void setRlSuggestedAction(String v)              { this.rlSuggestedAction = v; }

    public String getAdminDecision()                        { return adminDecision; }
    public void setAdminDecision(String v)                  { this.adminDecision = v; }

    public Double getAdminInterestRate()                    { return adminInterestRate; }
    public void setAdminInterestRate(Double v)              { this.adminInterestRate = v; }

    public LoanOutcome getActualOutcome()                  { return actualOutcome; }
    public void setActualOutcome(LoanOutcome v)            { this.actualOutcome = v; }

    public Double getRewardReceived()                      { return rewardReceived; }
    public void setRewardReceived(Double v)                { this.rewardReceived = v; }

    public Boolean getFeedbackGiven()                      { return feedbackGiven; }
    public void setFeedbackGiven(Boolean v)                { this.feedbackGiven = v; }

    public User getReviewedBy()                            { return reviewedBy; }
    public void setReviewedBy(User v)                      { this.reviewedBy = v; }

    public LocalDateTime getReviewedAt()                   { return reviewedAt; }
    public void setReviewedAt(LocalDateTime v)             { this.reviewedAt = v; }

    public String getReviewNotes()                         { return reviewNotes; }
    public void setReviewNotes(String v)                   { this.reviewNotes = v; }

    public LocalDateTime getCreatedAt()                    { return createdAt; }
    public void setCreatedAt(LocalDateTime v)              { this.createdAt = v; }

    public LocalDateTime getUpdatedAt()                    { return updatedAt; }
    public void setUpdatedAt(LocalDateTime v)              { this.updatedAt = v; }
}