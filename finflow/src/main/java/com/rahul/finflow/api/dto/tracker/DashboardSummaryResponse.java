package com.rahul.finflow.api.dto.tracker;

import java.math.BigDecimal;
import java.util.List;

public record DashboardSummaryResponse(
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal netBalance,
        List<CategorySpendResponse> categoryBreakDown,
        List<ExpenseResponse> recentTransactions,
        List<ActiveGoalResponse> activeGoals
) {
}
