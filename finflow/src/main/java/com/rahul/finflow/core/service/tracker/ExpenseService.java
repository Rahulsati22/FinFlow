package com.rahul.finflow.core.service.tracker;
import com.rahul.finflow.api.dto.tracker.ExpenseRequest;
import com.rahul.finflow.api.dto.tracker.ExpenseResponse;
import com.rahul.finflow.infrastructure.persistence.entity.UserEntity;
import com.rahul.finflow.infrastructure.persistence.entity.tracker.CategoryEntity;
import com.rahul.finflow.infrastructure.persistence.entity.tracker.ExpenseEntity;
import com.rahul.finflow.infrastructure.persistence.repository.UserRepository;
import com.rahul.finflow.infrastructure.persistence.repository.tracker.CategoryRepository;
import com.rahul.finflow.infrastructure.persistence.repository.tracker.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public ExpenseResponse addExpense(ExpenseRequest request, String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CategoryEntity category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Build and save the expense
        ExpenseEntity expense = ExpenseEntity.builder()
                .user(user)
                .category(category)
                .amount(request.amount())
                .description(request.description())
                .expenseDate(request.expenseDate())
                .build();

        ExpenseEntity savedExpense = expenseRepository.save(expense);
        return mapToResponse(savedExpense);
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponse> getUserExpenses(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return expenseRepository.findAllByUserIdOrderByExpenseDateDesc(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteExpense(UUID expenseId, String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ExpenseEntity expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        // CRITICAL SECURITY CHECK: Ensure the user actually owns this expense!
        if (!expense.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You do not have permission to delete this expense");
        }

        expenseRepository.delete(expense);
    }

    // Helper to map Entity to DTO safely handling potential null categories
    private ExpenseResponse mapToResponse(ExpenseEntity entity) {
        return new ExpenseResponse(
                entity.getId(),
                entity.getAmount(),
                entity.getDescription(),
                entity.getExpenseDate(),
                entity.getCategory() != null ? entity.getCategory().getId() : null,
                entity.getCategory() != null ? entity.getCategory().getName() : "Uncategorized",
                entity.getCategory() != null ? entity.getCategory().getIcon() : "help-circle"
        );
    }
}