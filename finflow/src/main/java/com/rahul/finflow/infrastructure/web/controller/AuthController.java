package com.rahul.finflow.infrastructure.web.controller;

import com.rahul.finflow.application.service.UserService;
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
        refreshCookie.setPath("/api/v1/auth/refresh");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60);

        response.addCookie(refreshCookie);


        return ResponseEntity.status(HttpStatus.OK).body(authResponse);
    }
}