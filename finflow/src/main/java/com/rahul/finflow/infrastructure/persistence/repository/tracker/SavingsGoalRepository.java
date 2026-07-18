package com.rahul.finflow.infrastructure.persistence.repository.tracker;

import com.rahul.finflow.infrastructure.persistence.entity.tracker.SavingsGoalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SavingsGoalRepository extends JpaRepository<SavingsGoalEntity, UUID> {
    List<SavingsGoalEntity> findAllByUserIdOrderByCreatedAtDesc(UUID userId);
}