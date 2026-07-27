package com.rahul.finflow.infrastructure.persistence.repository;

import com.rahul.finflow.infrastructure.persistence.entity.tracker.DebtLedgerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DebtLedgerRepository extends JpaRepository<DebtLedgerEntity, UUID> {
    List<DebtLedgerEntity> findByGroupId(UUID groupId);
}
