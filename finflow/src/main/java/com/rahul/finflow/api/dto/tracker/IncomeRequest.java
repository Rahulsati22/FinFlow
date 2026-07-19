package com.rahul.finflow.api.dto.tracker;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record IncomeRequest(
        @NotNull(message="Amount is required")
        @DecimalMin(value="0.01", message="Amount must be greater than 0")
        BigDecimal amount,

        @NotBlank(message="Source of income is required")
        String source,

        @NotNull(message="Income date is required")
        LocalDate incomeDate
) {
}
