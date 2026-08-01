package com.rahul.finflow.api.controller;
import com.rahul.finflow.api.dto.group.*;
import com.rahul.finflow.core.service.GroupExpenseService;
import com.rahul.finflow.core.service.GroupService;
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
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final GroupExpenseService groupExpenseService; // <-- Added the new service

    @PostMapping
    public ResponseEntity<GroupResponse> createGroup(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateGroupRequest request) {

        String creatorEmail = userDetails.getUsername();
        return ResponseEntity.ok(groupService.createGroup(creatorEmail, request));
    }

    // <-- New endpoint for shared expenses
    @PostMapping("/expenses")
    public ResponseEntity<String> addSharedExpense(
            @Valid @RequestBody SharedExpenseRequest request) {

        groupExpenseService.addSharedExpense(request);

        return ResponseEntity.status(HttpStatus.CREATED).body("Shared expense added successfully.");
    }

    // Add this to your existing GroupController class
    @GetMapping("/{groupId}/balances")
    public ResponseEntity<List<GroupBalanceResponse>> getGroupBalances(
            @PathVariable UUID groupId) {
        List<GroupBalanceResponse> balances = groupExpenseService.calculateGroupBalances(groupId);
        return ResponseEntity.ok(balances);
    }


    // Add this inside GroupController

    @GetMapping("/{groupId}/simplify-debts")
    public ResponseEntity<List<SimplifiedDebtResponse>> getSimplifiedDebts(
            @PathVariable UUID groupId) {

        List<SimplifiedDebtResponse> simplifiedDebts = groupExpenseService.simplifyGroupDebts(groupId);

        return ResponseEntity.ok(simplifiedDebts);
    }

    // Add this to your existing GroupController class

    @PostMapping("/settlements")
    public ResponseEntity<String> settleDebt(
            @Valid @RequestBody SettlementRequest request) {

        groupExpenseService.settleDebt(request);

        return ResponseEntity.ok("Debt settled successfully.");
    }
}