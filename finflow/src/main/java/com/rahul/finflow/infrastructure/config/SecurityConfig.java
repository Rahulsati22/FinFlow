package com.rahul.finflow.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Disable CSRF (Cross-Site Request Forgery)
                // Why? CSRF protection is for session-based (cookie) authentication.
                // We will use JWTs in the Authorization header, rendering CSRF attacks moot.
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Session Management
                // Why? REST APIs should be stateless. No JSESSIONID cookies.
                // Every request must bring its own token later on.
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 3. Route Authorization
                .authorizeHttpRequests(auth -> auth
                        // Allow unauthenticated access to all auth endpoints (register, login)
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        // Require authentication for absolutely everything else
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}