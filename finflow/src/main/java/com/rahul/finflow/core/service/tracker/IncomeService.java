package com.rahul.finflow.core.service.tracker;

import com.rahul.finflow.api.dto.tracker.IncomeRequest;
import com.rahul.finflow.api.dto.tracker.IncomeResponse;
import com.rahul.finflow.infrastructure.persistence.entity.UserEntity;
import com.rahul.finflow.infrastructure.persistence.entity.tracker.IncomeEntity;
import com.rahul.finflow.infrastructure.persistence.repository.UserRepository;
import com.rahul.finflow.infrastructure.persistence.repository.tracker.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final IncomeRepository incomeRepository;
    private final UserRepository userRepository;

    @Transactional
    public IncomeResponse addIncome(IncomeRequest request, String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        IncomeEntity income = IncomeEntity.builder()
                .user(user)
                .amount(request.amount())
                .source(request.source())
                .incomeDate(request.incomeDate())
                .build();

        IncomeEntity savedIncome = incomeRepository.save(income);
        return mapToResponse(savedIncome);
    }

    @Transactional(readOnly = true)
    public List<IncomeResponse> getUserIncomes(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return incomeRepository.findAllByUserIdOrderByIncomeDateDesc(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteIncome(UUID incomeId, String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        IncomeEntity income = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new RuntimeException("Income not found"));

        if (!income.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You do not have permission to delete this income");
        }

        incomeRepository.delete(income);
    }

    private IncomeResponse mapToResponse(IncomeEntity entity) {
        return new IncomeResponse(
                entity.getId(),
                entity.getAmount(),
                entity.getSource(),
                entity.getIncomeDate()
        );
    }
}