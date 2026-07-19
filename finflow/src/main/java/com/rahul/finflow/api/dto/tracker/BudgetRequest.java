package com.rahul.finflow.api.dto.tracker;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record BudgetRequest(
        @NotNull(message = "Category ID is required")
        UUID categoryId,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Budget must be greater than zero")
        BigDecimal amount,

        @NotNull(message = "Month is required")
        @Min(1) @Max(12)
        Integer month,

        @NotNull(message = "Year is required")
        Integer year
) {}