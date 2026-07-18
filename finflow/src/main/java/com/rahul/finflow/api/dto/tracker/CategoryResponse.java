package com.rahul.finflow.api.dto.tracker;

import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name,
        String icon,
        boolean isSystemDefault
) {
}
