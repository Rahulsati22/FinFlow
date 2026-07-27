package com.rahul.finflow.core.service;

import com.rahul.finflow.api.dto.group.SharedExpenseRequest;
import com.rahul.finflow.infrastructure.persistence.entity.tracker.CategoryEntity;
import com.rahul.finflow.infrastructure.persistence.entity.tracker.ExpenseEntity;
import com.rahul.finflow.infrastructure.persistence.entity.UserEntity;
import com.rahul.finflow.infrastructure.persistence.entity.tracker.DebtLedgerEntity;
import com.rahul.finflow.infrastructure.persistence.entity.tracker.GroupEntity;
import com.rahul.finflow.infrastructure.persistence.entity.tracker.SplitType;
import com.rahul.finflow.infrastructure.persistence.repository.*;
import com.rahul.finflow.infrastructure.persistence.repository.tracker.CategoryRepository;
import com.rahul.finflow.infrastructure.persistence.repository.tracker.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupExpenseService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final DebtLedgerRepository debtLedgerRepository;

    //person is adding a shared expense to a group
    @Transactional
    public void addSharedExpense(SharedExpenseRequest request) {

        // 1. Fetch all required entities safely
        GroupEntity group = groupRepository.findById(request.groupId())
                .orElseThrow(() -> new RuntimeException("Group not found"));

        UserEntity payer = userRepository.findById(request.paidByUserId())
                .orElseThrow(() -> new RuntimeException("Payer not found"));

        CategoryEntity category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // 2. Save the main Expense record
        ExpenseEntity expense = ExpenseEntity.builder()
                .user(payer) // The person who paid owns the main expense record
                .category(category)
                .group(group)
                .amount(request.totalAmount())
                .description(request.description())
                .expenseDate(request.expenseDate())
                .splitType(request.splitType())
                .build();

        expenseRepository.save(expense);

        // 3. Handle the Math Engine based on the split type
        if (request.splitType() == SplitType.EQUAL) {
            processEqualSplit(group, payer, request.totalAmount());
        } else {
            // We will implement EXACT and PERCENTAGE later
            throw new UnsupportedOperationException("Split type not yet supported");
        }
    }

    //splitting the money and adding to the debt ledger
    private void processEqualSplit(GroupEntity group, UserEntity payer, BigDecimal totalAmount) {
        List<UserEntity> members = group.getMembers();
        int memberCount = members.size();

        if (memberCount == 0) return;

        // Calculate exact share per person, rounding to 2 decimal places
        BigDecimal splitAmount = totalAmount.divide(
                BigDecimal.valueOf(memberCount), 2, RoundingMode.HALF_UP
        );

        List<DebtLedgerEntity> newDebts = new ArrayList<>();

        for (UserEntity member : members) {
            // The payer doesn't owe themselves money
            if (!member.getId().equals(payer.getId())) {

                DebtLedgerEntity debt = DebtLedgerEntity.builder()
                        .lender(payer)
                        .borrower(member)
                        .group(group)
                        .amount(splitAmount)
                        .build();

                newDebts.add(debt);
            }
        }

        // Save all generated debts to the database in one batch
        debtLedgerRepository.saveAll(newDebts);
    }
}