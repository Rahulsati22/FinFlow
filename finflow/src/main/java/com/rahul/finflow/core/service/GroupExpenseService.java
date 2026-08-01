package com.rahul.finflow.core.service;

import com.rahul.finflow.api.dto.group.*;
import com.rahul.finflow.infrastructure.persistence.entity.tracker.CategoryEntity;
import com.rahul.finflow.infrastructure.persistence.entity.tracker.ExpenseEntity;
import com.rahul.finflow.infrastructure.persistence.entity.UserEntity;
import com.rahul.finflow.infrastructure.persistence.entity.tracker.DebtLedgerEntity;
import com.rahul.finflow.infrastructure.persistence.entity.tracker.GroupEntity;
import com.rahul.finflow.infrastructure.persistence.entity.tracker.SplitType;
import com.rahul.finflow.infrastructure.persistence.repository.*;
import com.rahul.finflow.infrastructure.persistence.repository.tracker.CategoryRepository;
import com.rahul.finflow.infrastructure.persistence.repository.tracker.ExpenseRepository;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

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
//        if (request.splitType() == SplitType.EQUAL) {
//            processEqualSplit(group, payer, request.totalAmount());
//        } else {
//            // We will implement EXACT and PERCENTAGE later
//            throw new UnsupportedOperationException("Split type not yet supported");
//        }


        switch (request.splitType()) {
            case EQUAL -> processEqualSplit(group, payer, request.totalAmount());
            case EXACT -> processExactSplit(group, payer, request.totalAmount(), request.splits());
            case PERCENTAGE -> processPercentageSplit(group, payer, request.totalAmount(), request.splits());
            default -> throw new UnsupportedOperationException("Split type not yet supported");
        }
    }

    private void processExactSplit(GroupEntity group, UserEntity payer, BigDecimal totalAmount, List<SplitDetail> splits) {
        if (splits == null || splits.isEmpty()) {
            throw new IllegalArgumentException("Splits are required for EXACT split type");
        }

        // Validate that the individual splits add up to the total amount
        BigDecimal sumOfSplits = splits.stream()
                .map(SplitDetail::value)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (sumOfSplits.compareTo(totalAmount) != 0) {
            throw new IllegalArgumentException("Sum of exact splits must equal the total amount");
        }

        List<DebtLedgerEntity> newDebts = new ArrayList<>();

        for (SplitDetail split : splits) {
            if (!split.userId().equals(payer.getId())) {
                UserEntity borrower = userRepository.findById(split.userId())
                        .orElseThrow(() -> new RuntimeException("User in split not found"));

                newDebts.add(DebtLedgerEntity.builder()
                        .lender(payer)
                        .borrower(borrower)
                        .group(group)
                        .amount(split.value()) // The value is the exact dollar amount owed
                        .build());
            }
        }
        debtLedgerRepository.saveAll(newDebts);
    }

    // --- PERCENTAGE SPLIT MATH ---
    private void processPercentageSplit(GroupEntity group, UserEntity payer, BigDecimal totalAmount, List<SplitDetail> splits) {
        if (splits == null || splits.isEmpty()) {
            throw new IllegalArgumentException("Splits are required for PERCENTAGE split type");
        }

        // Validate that the percentages add up to 100%
        BigDecimal sumOfPercentages = splits.stream()
                .map(SplitDetail::value)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (sumOfPercentages.compareTo(new BigDecimal("100.00")) != 0 && sumOfPercentages.compareTo(new BigDecimal("100")) != 0) {
            throw new IllegalArgumentException("Sum of percentages must equal 100");
        }

        List<DebtLedgerEntity> newDebts = new ArrayList<>();

        for (SplitDetail split : splits) {
            if (!split.userId().equals(payer.getId())) {
                UserEntity borrower = userRepository.findById(split.userId())
                        .orElseThrow(() -> new RuntimeException("User in split not found"));

                // Math: (Total Amount * Percentage) / 100
                BigDecimal calculatedOwedAmount = totalAmount.multiply(split.value())
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

                newDebts.add(DebtLedgerEntity.builder()
                        .lender(payer)
                        .borrower(borrower)
                        .group(group)
                        .amount(calculatedOwedAmount)
                        .build());
            }
        }
        debtLedgerRepository.saveAll(newDebts);
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




    @Transactional(readOnly = true)
    public List<GroupBalanceResponse> calculateGroupBalances(UUID groupId) {

        // 1. Fetch all ledgers (debts) for this specific group
        List<DebtLedgerEntity> ledgers = debtLedgerRepository.findByGroupId(groupId);

        // 2. Map to hold the running balance for each user
        Map<UserEntity, BigDecimal> userBalances = new HashMap<>();

        // 3. Crunch the numbers
        for (DebtLedgerEntity ledger : ledgers) {
            UserEntity lender = ledger.getLender();
            UserEntity borrower = ledger.getBorrower();
            BigDecimal amount = ledger.getAmount();

            // Lender gets money back (+ balance)
            userBalances.put(lender, userBalances.getOrDefault(lender, BigDecimal.ZERO).add(amount));

            // Borrower owes money (- balance)
            userBalances.put(borrower, userBalances.getOrDefault(borrower, BigDecimal.ZERO).subtract(amount));
        }

        // 4. Convert the Map into a clean List of DTOs for the API response
        List<GroupBalanceResponse> responseList = new ArrayList<>();

        for (Map.Entry<UserEntity, BigDecimal> entry : userBalances.entrySet()) {
            UserEntity user = entry.getKey();
            BigDecimal netBalance = entry.getValue();

            responseList.add(new GroupBalanceResponse(
                    user.getId(),
                    user.getFirstName(),
                    user.getLastName(),
                    netBalance
            ));
        }

        return responseList;
    }



    //writing the algorithm that will return the simplified debts
    @Transactional(readOnly = true)
    public List<SimplifiedDebtResponse> simplifyGroupDebts(UUID groupId) {
        // 1. Fetch all raw ledgers (debts) for this group
        List<DebtLedgerEntity> ledgers = debtLedgerRepository.findByGroupId(groupId);

        // 2. Calculate net balance for each user
        Map<UserEntity, BigDecimal> balances = new HashMap<>();

        for (DebtLedgerEntity ledger : ledgers) {
            UserEntity lender = ledger.getLender();
            UserEntity borrower = ledger.getBorrower();
            BigDecimal amount = ledger.getAmount();

            balances.put(lender, balances.getOrDefault(lender, BigDecimal.ZERO).add(amount));
            balances.put(borrower, balances.getOrDefault(borrower, BigDecimal.ZERO).subtract(amount));
        }

        // 3. Separate users into Debtors (negative balance) and Creditors (positive balance)
        List<UserEntity> creditors = new ArrayList<>();
        List<UserEntity> debtors = new ArrayList<>();

        for (Map.Entry<UserEntity, BigDecimal> entry : balances.entrySet()) {
            if (entry.getValue().compareTo(BigDecimal.ZERO) > 0) {
                creditors.add(entry.getKey());
            } else if (entry.getValue().compareTo(BigDecimal.ZERO) < 0) {
                debtors.add(entry.getKey());
            }
        }

        // 4. The Algorithm: Greedily match debtors to creditors
        List<SimplifiedDebtResponse> simplifiedDebts = new ArrayList<>();

        int i = 0; // Creditor pointer
        int j = 0; // Debtor pointer

        while (i < creditors.size() && j < debtors.size()) {
            UserEntity creditor = creditors.get(i);
            UserEntity debtor = debtors.get(j);

            BigDecimal creditAmount = balances.get(creditor);
            BigDecimal debtAmount = balances.get(debtor).abs(); // Convert negative to positive for comparison

            // Find the minimum of the two amounts
            BigDecimal settledAmount = creditAmount.min(debtAmount);

            // Record this simplified transaction
            simplifiedDebts.add(new SimplifiedDebtResponse(
                    creditor.getId(),
                    creditor.getFirstName(),
                    debtor.getId(),
                    debtor.getFirstName(),
                    settledAmount
            ));

            // Update their running balances
            balances.put(creditor, creditAmount.subtract(settledAmount));
            balances.put(debtor, balances.get(debtor).add(settledAmount)); // Adding to negative moves it toward zero

            // Move the pointers if someone's balance is fully settled
            if (balances.get(creditor).compareTo(BigDecimal.ZERO) == 0) {
                i++;
            }
            if (balances.get(debtor).compareTo(BigDecimal.ZERO) == 0) {
                j++;
            }
        }

        return simplifiedDebts;
    }


    // Add this inside GroupExpenseService

    @Transactional
    public void settleDebt(SettlementRequest request) {

        GroupEntity group = groupRepository.findById(request.groupId())
                .orElseThrow(() -> new RuntimeException("Group not found"));

        UserEntity payer = userRepository.findById(request.payerId())
                .orElseThrow(() -> new RuntimeException("Payer not found"));

        UserEntity receiver = userRepository.findById(request.receiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        // To settle a debt, we create a reverse entry.
        // The person paying the cash becomes the "lender" for this specific transaction,
        // which perfectly zeroes out their negative balance when the balances are recalculated.
        DebtLedgerEntity settlement = DebtLedgerEntity.builder()
                .lender(payer)
                .borrower(receiver)
                .group(group)
                .amount(request.amount())
                .build();

        debtLedgerRepository.save(settlement);
    }



}