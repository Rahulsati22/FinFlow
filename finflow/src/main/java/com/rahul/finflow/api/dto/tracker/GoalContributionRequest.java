package com.rahul.finflow.api.dto.tracker;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record GoalContributionRequest(
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Contribution must be greater than zero")
        BigDecimal amount
) {}