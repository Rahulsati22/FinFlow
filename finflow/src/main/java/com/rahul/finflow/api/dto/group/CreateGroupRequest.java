package com.rahul.finflow.api.dto.group;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;


//members are also required while creating the group with name and description
public record CreateGroupRequest(
        @NotBlank(message = "Group name is required") String name,
        String description,
        @NotNull(message = "Member emails list cannot be null") List<String> memberEmails
) {}