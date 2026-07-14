package com.rahul.finflow.infrastructure.config;

import com.rahul.finflow.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    private final UserRepository userRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        //UserDetails loadUserByUsername
        return username-> userRepository.findByEmail(username)
                .map(userEntity ->
                        User.builder()
                                .username(userEntity.getEmail())
                                .password(userEntity.getPasswordHash())
                                .roles("User")
                                .build()
                ).orElseThrow(()-> new UsernameNotFoundException("User not found"));
    }
}
