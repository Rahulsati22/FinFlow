package com.rahul.finflow.core.service.tracker;

import com.rahul.finflow.api.dto.tracker.SavingsGoalRequest;
import com.rahul.finflow.api.dto.tracker.SavingsGoalResponse;
import com.rahul.finflow.infrastructure.persistence.entity.UserEntity;
import com.rahul.finflow.infrastructure.persistence.entity.tracker.SavingsGoalEntity;
import com.rahul.finflow.infrastructure.persistence.repository.UserRepository;
import com.rahul.finflow.infrastructure.persistence.repository.tracker.SavingsGoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SavingsGoalService {

    private final SavingsGoalRepository savingsGoalRepository;
    private final UserRepository userRepository;

    @Transactional
    public SavingsGoalResponse addGoal(SavingsGoalRequest request, String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        SavingsGoalEntity goal = SavingsGoalEntity.builder()
                .user(user)
                .name(request.name())
                .targetAmount(request.targetAmount())
                .targetDate(request.targetDate())
                .build(); // currentAmount defaults to 0.00 via Entity setup

        return mapToResponse(savingsGoalRepository.save(goal));
    }

    @Transactional(readOnly = true)
    public List<SavingsGoalResponse> getUserGoals(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return savingsGoalRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public SavingsGoalResponse addFundsToGoal(UUID goalId, BigDecimal amountToAdd, String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        SavingsGoalEntity goal = savingsGoalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        // Security: Make sure they own this goal!
        if (!goal.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You do not have permission to update this goal");
        }

        BigDecimal newTotal = goal.getCurrentAmount().add(amountToAdd);

        // Strict Block if the new total exceeds the target
        if (newTotal.compareTo(goal.getTargetAmount()) > 0) {
            BigDecimal remaining = goal.getTargetAmount().subtract(goal.getCurrentAmount());
            throw new IllegalArgumentException("Contribution exceeds the goal! You only need " + remaining + " to complete this goal.");
        }

        goal.setCurrentAmount(newTotal);

        return mapToResponse(savingsGoalRepository.save(goal));
    }

    private SavingsGoalResponse mapToResponse(SavingsGoalEntity entity) {
        return new SavingsGoalResponse(
                entity.getId(),
                entity.getName(),
                entity.getTargetAmount(),
                entity.getCurrentAmount(),
                entity.getTargetDate()
        );
    }
}