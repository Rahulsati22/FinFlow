package com.rahul.finflow.core.service.tracker;

import com.rahul.finflow.api.dto.tracker.BudgetRequest;
import com.rahul.finflow.api.dto.tracker.BudgetResponse;
import com.rahul.finflow.infrastructure.persistence.entity.UserEntity;
import com.rahul.finflow.infrastructure.persistence.entity.tracker.BudgetEntity;
import com.rahul.finflow.infrastructure.persistence.entity.tracker.CategoryEntity;
import com.rahul.finflow.infrastructure.persistence.repository.UserRepository;
import com.rahul.finflow.infrastructure.persistence.repository.tracker.BudgetRepository;
import com.rahul.finflow.infrastructure.persistence.repository.tracker.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public BudgetResponse setBudget(BudgetRequest request, String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CategoryEntity category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Check if a budget already exists for this category in this month/year
        Optional<BudgetEntity> existingBudget = budgetRepository
                .findByUserIdAndCategoryIdAndYearAndMonth(user.getId(), category.getId(), request.year(), request.month());

        BudgetEntity budget;
        if (existingBudget.isPresent()) {
            // Update existing budget
            budget = existingBudget.get();
            budget.setAmount(request.amount());
        } else {
            // Create new budget
            budget = BudgetEntity.builder()
                    .user(user)
                    .category(category)
                    .amount(request.amount())
                    .month(request.month())
                    .year(request.year())
                    .build();
        }

        return mapToResponse(budgetRepository.save(budget));
    }

    @Transactional(readOnly = true)
    public List<BudgetResponse> getUserBudgets(Integer year, Integer month, String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return budgetRepository.findAllByUserIdAndYearAndMonth(user.getId(), year, month)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private BudgetResponse mapToResponse(BudgetEntity entity) {
        return new BudgetResponse(
                entity.getId(),
                entity.getCategory().getId(),
                entity.getCategory().getName(),
                entity.getAmount(),
                entity.getMonth(),
                entity.getYear()
        );
    }
}