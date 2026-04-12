package com.loanguard.service;

import com.loanguard.dto.*;
import com.loanguard.exception.ApiException;
import com.loanguard.model.*;
import com.loanguard.model.UserRole;
import com.loanguard.repository.LoanApplicationRepository;
import com.loanguard.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class LoanService {

    private final LoanApplicationRepository loanRepo;
    private final RLClientService rlClient;
    private final NotificationService notifService;
    private final AuditService auditService;
    private final UserRepository userRepo;

    public LoanService(LoanApplicationRepository loanRepo, RLClientService rlClient,
                       NotificationService notifService, AuditService auditService,
                       UserRepository userRepo) {
        this.loanRepo     = loanRepo;
        this.rlClient     = rlClient;
        this.notifService = notifService;
        this.auditService = auditService;
        this.userRepo     = userRepo;
    }

    private String generateRef() {
        return "LG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private static class RateAdjustment {
        final double rate;
        final boolean changed;
        final String message;

        RateAdjustment(double rate, boolean changed, String message) {
            this.rate = rate;
            this.changed = changed;
            this.message = message;
        }
    }

    private RateAdjustment validateAndAdjustInterest(double rlRate, double defaultProb, double dti, double lti) {
        String riskCategory = defaultProb < 0.20 ? "LOW" : defaultProb <= 0.40 ? "MEDIUM" : "HIGH";
        double minRate = 8.0;
        double maxRate = 18.0;

        switch (riskCategory) {
            case "LOW" -> { minRate = 8.0; maxRate = 11.0; }
            case "MEDIUM" -> { minRate = 11.0; maxRate = 14.0; }
            case "HIGH" -> { minRate = 14.0; maxRate = 18.0; }
        }

        double adjusted = rlRate;
        StringBuilder note = new StringBuilder("Risk=" + riskCategory + "; RLRate=" + rlRate + "%");

        if (rlRate < minRate) {
            adjusted = minRate;
            note.append("; OutOfRange -> set to min=" + minRate + "%");
        } else if (rlRate > maxRate) {
            adjusted = maxRate;
            note.append("; OutOfRange -> set to max=" + maxRate + "%");
        } else {
            note.append("; WithinRange");
        }

        double modifier = 0.0;
        if (dti > 40.0) {
            modifier += 0.5;
            note.append("; HighDTI(" + String.format("%.1f", dti) + "%) -> +0.5");
        } else if (dti < 20.0) {
            modifier -= 0.5;
            note.append("; LowDTI(" + String.format("%.1f", dti) + "%) -> -0.5");
        }

        if (lti > 40.0) {
            modifier += 0.3;
            note.append("; HighLTI(" + String.format("%.1f", lti) + "%) -> +0.3");
        } else if (lti < 20.0) {
            modifier -= 0.3;
            note.append("; LowLTI(" + String.format("%.1f", lti) + "%) -> -0.3");
        }

        if (modifier != 0.0) {
            double tuned = adjusted + modifier;
            tuned = Math.max(minRate, Math.min(maxRate, tuned));
            if (Math.abs(tuned - adjusted) > 0.001) {
                note.append("; ModifierApplied -> " + String.format("%.2f", adjusted) + "% to " + String.format("%.2f", tuned) + "%");
                adjusted = tuned;
            }
        }

        boolean changed = Math.abs(adjusted - rlRate) > 0.001;

        return new RateAdjustment(adjusted, changed, note.toString());
    }

    @Transactional
    public LoanResponse processApplication(User user, LoanRequest req, String ip) {

        RLDecisionResponse rl = rlClient.decide(
                req.annualIncome(), req.loanAmount(),
                req.existingDebt(), req.employmentYears());

        LoanStatus status;
        RiskLevel riskLevel;

        // Initial decision from RL output
        switch (rl.action()) {
            case "APPROVE_STANDARD", "APPROVE_MODERATE", "APPROVE_HIGH" -> {
                status = LoanStatus.APPROVED;
                riskLevel = rl.defaultProbability() < 0.3 ? RiskLevel.GREEN :
                        rl.defaultProbability() < 0.5 ? RiskLevel.YELLOW : RiskLevel.RED;
            }
            case "MANUAL_REVIEW" -> {
                status = LoanStatus.UNDER_REVIEW;
                riskLevel = rl.defaultProbability() < 0.3 ? RiskLevel.GREEN :
                        rl.defaultProbability() < 0.5 ? RiskLevel.YELLOW : RiskLevel.RED;
            }
            default -> {
                status = LoanStatus.REJECTED;
                riskLevel = RiskLevel.RED;
            }
        }

        double dti = req.existingDebt() / req.annualIncome() * 100.0;
        double lti = req.loanAmount() / req.annualIncome() * 100.0;

        boolean lowRiskFastApproval = "MANUAL_REVIEW".equals(rl.action())
                && rl.defaultProbability() < 0.15
                && dti < 20.0
                && lti < 30.0;

        boolean doAdminReview = rl.needsAdminReview();
        String confidenceLevel = rl.confidenceLevel();

        if (lowRiskFastApproval) {
            status = LoanStatus.APPROVED;
            riskLevel = RiskLevel.GREEN;
            doAdminReview = false;
            confidenceLevel = "High (Rule-Assisted Approval)";
        } else {
            double confScore = rl.confidence() * 100.0;
            if (confScore >= 70.0) {
                // high confidence: keep status as derived
            } else if (confScore >= 40.0) {
                // medium confidence logic per risk
                if (riskLevel == RiskLevel.GREEN) {
                    status = LoanStatus.APPROVED;
                    doAdminReview = false;
                } else if (riskLevel == RiskLevel.YELLOW) {
                    status = LoanStatus.UNDER_REVIEW;
                    doAdminReview = true;
                } else {
                    status = LoanStatus.UNDER_REVIEW;
                    doAdminReview = true;
                }
            } else {
                // low confidence => manual review
                status = LoanStatus.UNDER_REVIEW;
                doAdminReview = true;
            }
        }

        LoanApplication app = new LoanApplication();
        app.setNeedsAdminReview(doAdminReview);

        app.setUser(user);
        app.setApplicantName(user.getFullName());
        app.setApplicantEmail(user.getEmail());
        app.setApplicationRef(generateRef());
        app.setAnnualIncome(req.annualIncome());
        app.setLoanAmount(req.loanAmount());
        app.setExistingDebt(req.existingDebt());
        app.setEmploymentYears(req.employmentYears());
        app.setLoanPurpose(req.loanPurpose());
        app.setLoanTermMonths(req.loanTermMonths() != null ? req.loanTermMonths() : 36);
        app.setRlAction(rl.action());
        app.setRlSuggestedAction(rl.action());

        RateAdjustment adj = validateAndAdjustInterest(rl.interestRate(), rl.defaultProbability(), dti, lti);

        app.setOfferedInterestRate(adj.rate);
        app.setDefaultProbability(rl.defaultProbability());
        app.setRiskLevel(riskLevel);
        app.setStatus(status);

        if (lowRiskFastApproval) {
            app.setAdvisoryMessage("Low risk profile. Loan approved. Final interest rate may be adjusted by admin.");
        } else if (status == LoanStatus.APPROVED && riskLevel == RiskLevel.GREEN) {
            app.setAdvisoryMessage("Low risk profile. Loan approved with a responsible rate.");
        } else if (status == LoanStatus.UNDER_REVIEW) {
            if (riskLevel == RiskLevel.GREEN) {
                app.setAdvisoryMessage("Final interest rate confirmation in progress");
            } else if (riskLevel == RiskLevel.YELLOW) {
                app.setAdvisoryMessage("Application under review due to moderate risk");
            } else {
                app.setAdvisoryMessage("Manual review required before decision");
            }
        } else {
            app.setAdvisoryMessage(rl.advisoryMessage());
        }

        app.setRlState(rl.state());
        app.setQValues(rl.qValues().toString());
        app.setConfidenceLevel(confidenceLevel);
        app.setNeedsAdminReview(doAdminReview);
        app.setEscalationReason(rl.escalationReason());

        String interestLog = "RLRate=" + rl.interestRate() + "% -> finalRate=" + adj.rate + "%; " + adj.message;
        app.setAdminNote((app.getAdminNote() != null ? app.getAdminNote() + " | " : "") + "Interest post-processing applied. " + interestLog);

        app.setActualOutcome(LoanOutcome.PENDING);
        app.setFeedbackGiven(false);

        LoanApplication saved = loanRepo.save(app);

        if (adj.changed) {
            auditService.log(user, "INTEREST_RATE_ADJUSTED", "LOAN", saved.getId(),
                    interestLog + "; DTI=" + String.format("%.2f", dti) + "%; LTI=" + String.format("%.2f", lti) + "%", ip);
        } else {
            auditService.log(user, "INTEREST_RATE_VALIDATED", "LOAN", saved.getId(),
                    interestLog + "; DTI=" + String.format("%.2f", dti) + "%; LTI=" + String.format("%.2f", lti) + "%", ip);
        }

        String notifTitle;
        String notifMsg;

        if (doAdminReview) {
            if (riskLevel == RiskLevel.GREEN) {
                notifTitle = "Final interest rate confirmation in progress";
                notifMsg = "Your application " + saved.getApplicationRef() +
                        " is low-risk and is being reviewed for final pricing confirmation.";
            } else if (riskLevel == RiskLevel.YELLOW) {
                notifTitle = "Application under review due to moderate risk";
                notifMsg = "Your application " + saved.getApplicationRef() +
                        " is under review due to moderate risk metrics.";
            } else {
                notifTitle = "Manual review required before decision";
                notifMsg = "Your application " + saved.getApplicationRef() +
                        " requires manual review because the risk is high.";
            }
        } else if (status == LoanStatus.APPROVED) {
            notifTitle = "Loan Approved!";
            notifMsg = "Your application " + saved.getApplicationRef() +
                    " has been approved. Final interest rate may be adjusted by admin if needed.";
        } else {
            notifTitle = "Loan Decision";
            notifMsg = "Your application " + saved.getApplicationRef() + " has been processed. " +
                    rl.advisoryMessage();
        }

        notifService.send(user, notifTitle, notifMsg, status.name(), "/loans/" + saved.getId());

        if (doAdminReview) {
            notifyAdmins(saved);
        }

        auditService.log(user, "LOAN_APPLIED", "LOAN", saved.getId(),
                "Ref: " + saved.getApplicationRef() +
                ", RL Action: " + rl.action() +
                ", Confidence: " + confidenceLevel +
                ", Escalated: " + doAdminReview +
                ", Amount: " + req.loanAmount(), ip);

        return LoanResponse.fromEntity(saved);
    }

    private void notifyAdmins(LoanApplication app) {
        var admins = userRepo.findAll().stream()
                .filter(u -> u.getRole() == UserRole.ADMIN || u.getRole() == UserRole.LOAN_OFFICER)
                .toList();

        for (User admin : admins) {
            notifService.send(admin,
                    "⚠️ Escalated Application: " + app.getApplicationRef(),
                    "Application from " + app.getUser().getFullName() +
                            " for $" + String.format("%,.0f", app.getLoanAmount()) +
                            " has been escalated due to " + app.getConfidenceLevel() +
                            " RL agent confidence. RL suggested: " + app.getRlSuggestedAction() +
                            ". Please review.",
                    "ESCALATED",
                    "/admin/loans"
            );
        }
    }

    @Transactional
    public LoanResponse submitFeedback(User user, Long loanId, boolean defaulted, String ip) {
        LoanApplication app = loanRepo.findById(loanId)
                .orElseThrow(() -> new ApiException("Loan not found", HttpStatus.NOT_FOUND));

        if (!app.getUser().getId().equals(user.getId())) {
            throw new ApiException("Unauthorized", HttpStatus.FORBIDDEN);
        }
        if (app.getFeedbackGiven()) {
            throw new ApiException("Feedback already submitted", HttpStatus.CONFLICT);
        }

        app.setActualOutcome(defaulted ? LoanOutcome.DEFAULTED : LoanOutcome.REPAID);
        app.setFeedbackGiven(true);

        rlClient.sendFeedback(
                app.getAnnualIncome(), app.getLoanAmount(), app.getExistingDebt(),
                app.getEmploymentYears(), app.getRlAction(), defaulted,
                app.getOfferedInterestRate() != null ? app.getOfferedInterestRate() : 0.0);

        auditService.log(user, "FEEDBACK_GIVEN", "LOAN", loanId,
                "Outcome: " + (defaulted ? "DEFAULTED" : "REPAID"), ip);

        return LoanResponse.fromEntity(loanRepo.save(app));
    }

    @Transactional
    public LoanResponse reviewApplication(User officer, ReviewRequest req, String ip) {
        LoanApplication app = loanRepo.findById(req.loanId())
                .orElseThrow(() -> new ApiException("Loan not found", HttpStatus.NOT_FOUND));

        if (app.getStatus() != LoanStatus.UNDER_REVIEW) {
            throw new ApiException("Only UNDER_REVIEW applications can be reviewed", HttpStatus.BAD_REQUEST);
        }

        LoanStatus newStatus;
        RiskLevel newRisk;

        switch (req.decision().toUpperCase()) {
            case "APPROVE" -> { newStatus = LoanStatus.APPROVED; newRisk = RiskLevel.YELLOW; }
            case "REJECT"  -> { newStatus = LoanStatus.REJECTED;  newRisk = RiskLevel.RED;    }
            default -> throw new ApiException("Decision must be APPROVE or REJECT", HttpStatus.BAD_REQUEST);
        }

        app.setStatus(newStatus);
        app.setRiskLevel(newRisk);
        app.setReviewedBy(officer);
        app.setReviewedAt(LocalDateTime.now());
        app.setReviewNotes(req.notes());
        if (req.overrideInterestRate() != null) {
            app.setOfferedInterestRate(req.overrideInterestRate());
        }

        notifService.send(app.getUser(),
                "Application " + app.getApplicationRef() + " Reviewed",
                "Your application has been " + req.decision().toLowerCase() + "d by a loan officer." +
                        (req.notes() != null ? " Notes: " + req.notes() : ""),
                newStatus.name(), "/history");

        auditService.log(officer, "LOAN_REVIEWED", "LOAN", req.loanId(),
                "Decision: " + req.decision() + ", Notes: " + req.notes(), ip);

        return LoanResponse.fromEntity(loanRepo.save(app));
    }

    @Transactional(readOnly = true)
    public LoanResponse getById(Long id) {
        return LoanResponse.fromEntity(loanRepo.findById(id)
                .orElseThrow(() -> new ApiException("Loan not found", HttpStatus.NOT_FOUND)));
    }

    @Transactional(readOnly = true)
    public List<LoanResponse> getUserApplications(Long userId) {
        return loanRepo.findByUserIdOrderByCreatedAtDesc(userId).stream().map(LoanResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public Page<LoanResponse> getAllApplications(int page, int size) {
        return loanRepo.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size)).map(LoanResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<LoanResponse> getByStatus(LoanStatus status, int page, int size) {
        return loanRepo.findByStatusOrderByCreatedAtDesc(status, PageRequest.of(page, size)).map(LoanResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public List<LoanResponse> getEscalatedApplications() {
        return loanRepo.findByNeedsAdminReviewTrueAndStatusOrderByCreatedAtDesc(LoanStatus.UNDER_REVIEW)
                .stream().map(LoanResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public long getEscalatedCount() {
        return loanRepo.countByNeedsAdminReviewTrueAndStatus(LoanStatus.UNDER_REVIEW);
    }
}
