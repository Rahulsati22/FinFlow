package com.rahul.finflow.api.dto.group;


import java.math.BigDecimal;
import java.util.UUID;

public record GroupBalanceResponse(
        UUID userId,
        String firstName,
        String lastName,
        BigDecimal netBalance
) {
}
