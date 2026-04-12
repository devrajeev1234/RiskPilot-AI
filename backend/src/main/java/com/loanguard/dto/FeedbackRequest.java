package com.loanguard.dto;

import jakarta.validation.constraints.NotNull;

public record FeedbackRequest(
        @NotNull Long loanId,
        @NotNull Boolean defaulted
) {}