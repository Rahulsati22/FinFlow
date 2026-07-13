package com.rahul.finflow.infrastructure.web.controller;

import com.rahul.finflow.application.service.UserService;
import com.rahul.finflow.infrastructure.web.dto.UserRegistrationRequest;
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
}