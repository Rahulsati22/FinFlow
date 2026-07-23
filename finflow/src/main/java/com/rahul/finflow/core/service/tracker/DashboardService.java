package com.rahul.finflow.core.service.tracker;

import com.rahul.finflow.api.dto.tracker.ActiveGoalResponse;
import com.rahul.finflow.api.dto.tracker.CategorySpendResponse;
import com.rahul.finflow.api.dto.tracker.DashboardSummaryResponse;
import com.rahul.finflow.api.dto.tracker.ExpenseResponse;
import com.rahul.finflow.infrastructure.persistence.entity.UserEntity;
import com.rahul.finflow.infrastructure.persistence.entity.tracker.ExpenseEntity;
import com.rahul.finflow.infrastructure.persistence.entity.tracker.IncomeEntity;
import com.rahul.finflow.infrastructure.persistence.repository.UserRepository;
import com.rahul.finflow.infrastructure.persistence.repository.tracker.ExpenseRepository;
import com.rahul.finflow.infrastructure.persistence.repository.tracker.IncomeRepository;
import com.rahul.finflow.infrastructure.persistence.repository.tracker.SavingsGoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;
    private final SavingsGoalRepository savingsGoalRepository;

    @Transactional(readOnly = true)
    public DashboardSummaryResponse getDashboardSummary(int month, int year, String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 1. Calculate date range for the requested month
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());

        // 2. Fetch all incomes and expenses for this month
        List<IncomeEntity> incomes = incomeRepository.findAllByUserIdAndIncomeDateBetween(user.getId(), startDate, endDate);
        List<ExpenseEntity> expenses = expenseRepository.findAllByUserIdAndExpenseDateBetween(user.getId(), startDate, endDate);

        // 3. Calculate Totals
        BigDecimal totalIncome = incomes.stream()
                .map(IncomeEntity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = expenses.stream()
                .map(ExpenseEntity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netBalance = totalIncome.subtract(totalExpense);

        // 4. Calculate Category Breakdown (Group by Category)
        List<CategorySpendResponse> categoryBreakdown = expenses.stream()
                .collect(Collectors.groupingBy(ExpenseEntity::getCategory))
                .entrySet().stream()
                .map(entry -> new CategorySpendResponse(
                        entry.getKey().getId(),
                        entry.getKey().getName(),
                        entry.getKey().getIcon(),
                        entry.getValue().stream().map(ExpenseEntity::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add)
                ))
                .collect(Collectors.toList());

        // 5. Get Recent Transactions (Top 5)
        List<ExpenseResponse> recentTransactions = expenseRepository.findTop5ByUserIdOrderByExpenseDateDesc(user.getId())
                .stream()
                .map(e -> new ExpenseResponse(
                        e.getId(), e.getAmount(), e.getDescription(), e.getExpenseDate(),
                        e.getCategory().getId(), e.getCategory().getName(), e.getCategory().getIcon()
                )).collect(Collectors.toList());

        // 6. Get Top 3 Active Goals (Where currentAmount < targetAmount)
        List<ActiveGoalResponse> activeGoals = savingsGoalRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .filter(goal -> goal.getCurrentAmount().compareTo(goal.getTargetAmount()) < 0) // Only unfinished goals
                .limit(3)
                .map(goal -> {
                    // Calculate percentage (e.g., 50.5%)
                    double percentage = goal.getCurrentAmount()
                            .divide(goal.getTargetAmount(), 3, RoundingMode.HALF_UP)
                            .multiply(new BigDecimal("100"))
                            .doubleValue();

                    return new ActiveGoalResponse(
                            goal.getId(), goal.getName(), goal.getTargetAmount(), goal.getCurrentAmount(), percentage
                    );
                })
                .collect(Collectors.toList());

        // 7. Return the massive DTO!
        return new DashboardSummaryResponse(
                totalIncome, totalExpense, netBalance, categoryBreakdown, recentTransactions, activeGoals
        );
    }
}