package com.rahul.finflow.infrastructure.persistence.repository.tracker;

import com.rahul.finflow.infrastructure.persistence.entity.tracker.ExpenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, UUID> {
    // Fetches all expenses for a user, sorted with the newest first
    List<ExpenseEntity> findAllByUserIdOrderByExpenseDateDesc(UUID userId);
}