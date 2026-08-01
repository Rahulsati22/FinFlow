package com.rahul.finflow.api.dto.group;

import java.math.BigDecimal;
import java.util.UUID;

public record SimplifiedDebtResponse(
        UUID lenderId,
        String lenderName,
        UUID borrowerId,
        String borrowerName,
        BigDecimal amount
) {
}
