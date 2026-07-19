package com.rahul.finflow.api.controller.tracker;

import com.rahul.finflow.api.dto.tracker.GoalContributionRequest;
import com.rahul.finflow.api.dto.tracker.SavingsGoalRequest;
import com.rahul.finflow.api.dto.tracker.SavingsGoalResponse;
import com.rahul.finflow.core.service.tracker.SavingsGoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/goals")
@RequiredArgsConstructor
public class SavingsGoalController {

    private final SavingsGoalService savingsGoalService;

    @PostMapping
    public ResponseEntity<SavingsGoalResponse> createGoal(
            @Valid @RequestBody SavingsGoalRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savingsGoalService.addGoal(request, userDetails.getUsername()));
    }

    @GetMapping
    public ResponseEntity<List<SavingsGoalResponse>> getMyGoals(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(savingsGoalService.getUserGoals(userDetails.getUsername()));
    }

    @PatchMapping("/{id}/contribute")
    public ResponseEntity<SavingsGoalResponse> addFunds(
            @PathVariable UUID id,
            @Valid @RequestBody GoalContributionRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(
                savingsGoalService.addFundsToGoal(id, request.amount(), userDetails.getUsername())
        );
    }
}