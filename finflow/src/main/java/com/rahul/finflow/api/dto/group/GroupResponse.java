package com.rahul.finflow.api.dto.group;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record GroupResponse(
        UUID id, String name, String description, UUID createdBy,
        List<String> memberEmails, LocalDateTime createdAt
) {}