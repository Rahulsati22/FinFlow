package com.rahul.finflow.infrastructure.persistence.repository;

import com.rahul.finflow.infrastructure.persistence.entity.tracker.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface GroupRepository extends JpaRepository<GroupEntity, UUID> {
}
