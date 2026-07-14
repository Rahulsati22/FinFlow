package com.rahul.finflow.infrastructure.persistence.repository;

import com.rahul.finflow.infrastructure.persistence.entity.RefreshTokenEntity;
import com.rahul.finflow.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {

    Optional<RefreshTokenEntity> findByToken(String token);

    // We will use this to log a user out of all devices
    void deleteByUser(UserEntity user);
}