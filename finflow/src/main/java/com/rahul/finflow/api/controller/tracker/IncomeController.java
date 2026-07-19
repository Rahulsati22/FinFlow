package com.rahul.finflow.api.controller.tracker;

import com.rahul.finflow.api.dto.tracker.IncomeRequest;
import com.rahul.finflow.api.dto.tracker.IncomeResponse;
import com.rahul.finflow.core.service.tracker.IncomeService;
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
@RequestMapping("/api/v1/incomes")
@RequiredArgsConstructor
public class IncomeController {

    private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<IncomeResponse> addIncome(
            @Valid @RequestBody IncomeRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        IncomeResponse response = incomeService.addIncome(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<IncomeResponse>> getMyIncomes(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<IncomeResponse> responses = incomeService.getUserIncomes(userDetails.getUsername());
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncome(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        incomeService.deleteIncome(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}