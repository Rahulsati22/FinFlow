package com.rahul.finflow.api.dto.tracker;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ExpenseResponse(
        UUID id,
        BigDecimal amount,
        String description,
        LocalDate expenseDate,
        UUID categoryId,
        String categoryName,
        String categoryIcon
) {
}


