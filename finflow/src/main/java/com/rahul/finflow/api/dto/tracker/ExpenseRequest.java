package com.rahul.finflow.api.dto.tracker;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ExpenseRequest(
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        BigDecimal amount,

        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Category Id is required")
        UUID categoryId,

        @NotNull(message = "Expense date is required")
        LocalDate expenseDate
){}
