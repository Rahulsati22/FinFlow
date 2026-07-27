package com.rahul.finflow.api.dto.group;

import com.rahul.finflow.infrastructure.persistence.entity.tracker.SplitType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record SharedExpenseRequest(
        @NotNull(message = "Group ID is required")
        UUID groupId, // <--- Here is the Group ID you correctly pointed out we needed!

        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Total amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
        BigDecimal totalAmount,

        @NotNull(message = "Payer ID is required")
        UUID paidByUserId,

        @NotNull(message = "Category ID is required")
        UUID categoryId,

        @NotNull(message = "Split type is required")
        SplitType splitType,

        @NotNull(message = "Expense date is required")
        LocalDate expenseDate
) {}