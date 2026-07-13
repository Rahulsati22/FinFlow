package com.rahul.finflow.infrastructure.persistence.repository;

import com.rahul.finflow.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    // We will need this for login later
    Optional<UserEntity> findByEmail(String email);

    // We will need this to check if a user exists during registration
    boolean existsByEmail(String email);
}