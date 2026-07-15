package com.rahul.finflow.application.service;


import com.rahul.finflow.infrastructure.persistence.entity.RefreshTokenEntity;
import com.rahul.finflow.infrastructure.persistence.entity.UserEntity;
import com.rahul.finflow.infrastructure.persistence.repository.RefreshTokenRepository;
import com.rahul.finflow.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Value("${spring.application.security.jwt.refresh-token.expiration}")
    private long refreshTokenDurationMs;


    @Transactional
    public String createRefreshToken(String email) {
        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(()->new IllegalArgumentException("Invalid email"));
        RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                .user(userEntity)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .build();

        refreshTokenRepository.save(refreshTokenEntity);
        return refreshTokenEntity.getToken();
    }

    public RefreshTokenEntity verifyExpiration(RefreshTokenEntity refreshTokenEntity) {
        if (refreshTokenEntity.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(refreshTokenEntity);
            throw new IllegalArgumentException("Refresh token expired.Please login again.");
        }
        return refreshTokenEntity;
    }

    public RefreshTokenEntity findByToken(String token){
        return refreshTokenRepository.findByToken(token).orElseThrow(()->new IllegalArgumentException("Invalid refresh token"));
    }

    public void deleteByToken(String token){
        System.out.println(token + " deleted");
        refreshTokenRepository.findByToken(token)
                .ifPresent(refreshTokenRepository::delete);
    }
}


