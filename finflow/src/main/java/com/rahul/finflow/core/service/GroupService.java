package com.rahul.finflow.core.service;

import com.rahul.finflow.api.dto.group.CreateGroupRequest;
import com.rahul.finflow.api.dto.group.GroupResponse;
import com.rahul.finflow.infrastructure.persistence.entity.UserEntity;
import com.rahul.finflow.infrastructure.persistence.entity.tracker.GroupEntity;
import com.rahul.finflow.infrastructure.persistence.repository.GroupRepository;
import com.rahul.finflow.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    @Transactional
    public GroupResponse createGroup(String creatorEmail, CreateGroupRequest request) {

        // 1. Find the user creating the group
        UserEntity creator = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new RuntimeException("Creator not found"));

        // 2. Find all users matching the requested emails
        List<UserEntity> members = userRepository.findAllByEmailIn(request.memberEmails());

        // 3. Ensure the creator is always a member of their own group
        if (!members.contains(creator)) {
            members.add(creator);
        }

        // 4. Build and save the entity
        GroupEntity group = GroupEntity.builder()
                .name(request.name())
                .description(request.description())
                .createdBy(creator)
                .members(members)
                .build();

        GroupEntity savedGroup = groupRepository.save(group);

        return mapToResponse(savedGroup);
    }

    // Helper method to convert Entity to DTO
    private GroupResponse mapToResponse(GroupEntity group) {
        List<String> memberEmails = group.getMembers().stream()
                .map(UserEntity::getEmail)
                .collect(Collectors.toList());

        return new GroupResponse(
                group.getId(),
                group.getName(),
                group.getDescription(),
                group.getCreatedBy().getId(),
                memberEmails,
                group.getCreatedAt()
        );
    }
}