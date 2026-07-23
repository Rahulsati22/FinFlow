package com.rahul.finflow.infrastructure.persistence.repository.tracker;

import com.rahul.finflow.infrastructure.persistence.entity.tracker.IncomeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface IncomeRepository extends JpaRepository<IncomeEntity, UUID> {
    List<IncomeEntity> findAllByUserIdOrderByIncomeDateDesc(UUID userId);

    List<IncomeEntity> findAllByUserIdAndIncomeDateBetween(UUID userId, LocalDate startDate, LocalDate endDate);
}