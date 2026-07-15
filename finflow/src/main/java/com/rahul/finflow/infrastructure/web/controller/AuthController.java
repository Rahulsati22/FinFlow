package com.rahul.finflow.infrastructure.web.controller;

import com.rahul.finflow.application.service.JwtService;
import com.rahul.finflow.application.service.RefreshTokenService;
import com.rahul.finflow.application.service.UserService;
import com.rahul.finflow.infrastructure.persistence.entity.RefreshTokenEntity;
import com.rahul.finflow.infrastructure.persistence.entity.UserEntity;
import com.rahul.finflow.infrastructure.web.dto.AuthResponse;
import com.rahul.finflow.infrastructure.web.dto.LoginRequest;
import com.rahul.finflow.infrastructure.web.dto.UserRegistrationRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody UserRegistrationRequest request) {
        String responseMessage = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage);
    }

    @PostMapping("/login")
    //to attach cookies we use httpservlet response
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        AuthResponse authResponse = userService.login(request);

        Cookie refreshCookie = new Cookie("refresh_token", authResponse.getRefreshToken());
        refreshCookie.setHttpOnly(true); //javascript cannot read this
        refreshCookie.setSecure(false); //set to true in production when using https
        refreshCookie.setPath("/api/v1/auth");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60);

        response.addCookie(refreshCookie);


        return ResponseEntity.status(HttpStatus.OK).body(authResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@CookieValue(name="refresh_token", required = false) String refreshToken){
        if (refreshToken == null){
            throw new IllegalArgumentException("Refresh token is missing.Please log in");
        }

        RefreshTokenEntity refreshTokenEntity = refreshTokenService.findByToken(refreshToken);
        refreshTokenService.verifyExpiration(refreshTokenEntity);
        UserEntity user = refreshTokenEntity.getUser();

        String newAccessToken = jwtService.generateToken(user.getEmail());

        return ResponseEntity.ok(AuthResponse.builder().accessToken(newAccessToken).build());
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@CookieValue(name="refresh_token", required = false) String refreshToken, HttpServletResponse response) {
        System.out.println("Refresh token: " + refreshToken);
        if (refreshToken != null){
            refreshTokenService.deleteByToken(refreshToken);
        }

        Cookie clearCookie = new Cookie("refresh_token", null);
        clearCookie.setHttpOnly(true);
        clearCookie.setSecure(false);
        clearCookie.setPath("/api/v1/auth/logout");
        clearCookie.setMaxAge(0);
        response.addCookie(clearCookie);
        return ResponseEntity.ok("Logged out successfully");
    }
}