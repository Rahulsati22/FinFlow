package com.rahul.finflow.api.dto.tracker;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        @NotBlank(message = "Category name is required")
        @Size(max = 100, message = "Category name cannot exceed 100 characters")
        String name,
        @Size(max = 50, message = "Icon name is too long")
        String icon) {}
