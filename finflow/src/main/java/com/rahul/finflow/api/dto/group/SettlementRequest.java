package com.rahul.finflow.api.dto.group;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record SettlementRequest(
        @NotNull(message = "Group ID is required")
        UUID groupId,

        @NotNull(message = "Payer ID is required (who is paying the money)")
        UUID payerId,

        @NotNull(message = "Receiver ID is required (who is receiving the money)")
        UUID receiverId,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
        BigDecimal amount
) {}