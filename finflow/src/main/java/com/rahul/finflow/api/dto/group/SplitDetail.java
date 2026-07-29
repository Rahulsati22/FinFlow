package com.rahul.finflow.api.dto.group;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;


public record SplitDetail(
        @NotNull(message="User id is required")
        UUID userId,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Value must be greater than 0")
        BigDecimal value
) {
}
