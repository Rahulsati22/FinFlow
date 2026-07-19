package com.rahul.finflow.api.controller.tracker;

import com.rahul.finflow.api.dto.tracker.BudgetRequest;
import com.rahul.finflow.api.dto.tracker.BudgetResponse;
import com.rahul.finflow.core.service.tracker.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    public ResponseEntity<BudgetResponse> setBudget(
            @Valid @RequestBody BudgetRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(budgetService.setBudget(request, userDetails.getUsername()));
    }

    @GetMapping
    public ResponseEntity<List<BudgetResponse>> getBudgets(
            @RequestParam Integer year,
            @RequestParam Integer month,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(budgetService.getUserBudgets(year, month, userDetails.getUsername()));
    }
}