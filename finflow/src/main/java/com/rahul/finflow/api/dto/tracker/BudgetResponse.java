package com.rahul.finflow.api.dto.tracker;

import java.math.BigDecimal;
import java.util.UUID;

public record BudgetResponse(
        UUID id,
        UUID categoryId,
        String categoryName,
        BigDecimal amount,
        Integer month,
        Integer year
) {}