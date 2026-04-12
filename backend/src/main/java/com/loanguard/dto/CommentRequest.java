package com.loanguard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommentRequest(
        @NotNull Long loanId,
        @NotBlank String comment,
        Boolean isInternal
) {}