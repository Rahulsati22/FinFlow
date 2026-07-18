package com.rahul.finflow.infrastructure.persistence.repository.tracker;

import com.rahul.finflow.infrastructure.persistence.entity.tracker.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<CategoryEntity, UUID> {

    // Magic Query: Fetch system defaults (user is NULL) AND the user's custom categories
    @Query("SELECT c FROM CategoryEntity c WHERE c.user.id = :userId OR c.user IS NULL")
    List<CategoryEntity> findAllByUserIdIncludingDefaults(@Param("userId") UUID userId);
}