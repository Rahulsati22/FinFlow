package com.rahul.finflow.application.service;
import com.rahul.finflow.infrastructure.persistence.entity.UserEntity;
import com.rahul.finflow.infrastructure.persistence.repository.UserRepository;
import com.rahul.finflow.infrastructure.web.dto.AuthResponse;
import com.rahul.finflow.infrastructure.web.dto.LoginRequest;
import com.rahul.finflow.infrastructure.web.dto.UserRegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public String registerUser(UserRegistrationRequest request) {
        // 1. Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        // 2. Map DTO to Entity (We will use MapStruct later, manual for now)
        UserEntity newUser = UserEntity.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();

        // 3. Save to database
        userRepository.save(newUser);

        return "User registered successfully";
    }

    //take the email
    //find if email exist
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        // 1. Fetch user by email
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        // 2. Check if password matches
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            // Note: We use the exact same error message to prevent "Username Enumeration" attacks.
            throw new IllegalArgumentException("Invalid email or password");
        }

        // 3. Generate token
        String token = jwtService.generateToken(user.getEmail());
        String refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        // 4. Return response
        return AuthResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .build();
    }
}