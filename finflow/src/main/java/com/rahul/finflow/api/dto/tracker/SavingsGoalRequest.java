package com.rahul.finflow.api.dto.tracker;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SavingsGoalRequest(
        @NotBlank(message = "Goal name is required")
        String name,

        @NotNull(message = "Target amount is required")
        @DecimalMin(value = "1.00", message = "Target must be at least 1")
        BigDecimal targetAmount,

        LocalDate targetDate
) {}