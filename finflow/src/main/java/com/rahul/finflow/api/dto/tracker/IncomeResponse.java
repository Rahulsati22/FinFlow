package com.rahul.finflow.api.dto.tracker;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record IncomeResponse(
        UUID id,
        BigDecimal amount,
        String source,
        LocalDate incomeDate
) {
}
