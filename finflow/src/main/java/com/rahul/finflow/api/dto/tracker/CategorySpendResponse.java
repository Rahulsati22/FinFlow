package com.rahul.finflow.api.dto.tracker;

import java.math.BigDecimal;
import java.util.UUID;

public record CategorySpendResponse(UUID categoryId,
                                    String categoryName,
                                    String categoryIcon,
                                    BigDecimal totalSpent) {
}
