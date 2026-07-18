package com.rahul.finflow.infrastructure.persistence.repository.tracker;

import com.rahul.finflow.infrastructure.persistence.entity.tracker.BudgetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BudgetRepository extends JpaRepository<BudgetEntity, UUID> {
    // Fetch all budgets for a user in a specific month/year
    List<BudgetEntity> findAllByUserIdAndYearAndMonth(UUID userId, Integer year, Integer month);

    // Check if a specific budget already exists (used for our UNIQUE constraint)
    Optional<BudgetEntity> findByUserIdAndCategoryIdAndYearAndMonth(UUID userId, UUID categoryId, Integer year, Integer month);
}