package com.rahul.finflow.api.dto.tracker;

import java.math.BigDecimal;
import java.util.UUID;

public record ActiveGoalResponse(
        UUID id,
        String name,
        BigDecimal targetAmount,
        BigDecimal currentAmount,
        Double progressPercentage
) {
}
