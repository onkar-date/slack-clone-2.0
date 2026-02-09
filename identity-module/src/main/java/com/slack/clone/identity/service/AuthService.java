package com.slack.clone.identity.service;

import com.slack.clone.identity.dto.*;
import com.slack.clone.identity.entity.User;
import com.slack.clone.identity.mapper.UserMapper;
import com.slack.clone.identity.repository.UserRepository;
import com.slack.clone.identity.security.JwtService;
import com.slack.clone.shared.exception.UnauthorizedException;
import com.slack.clone.shared.exception.ValidationException;
import com.slack.clone.shared.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service for authentication and user management
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    /**
     * Register a new user
     */
    @Transactional
    public UserDTO register(RegisterRequest request) {
        log.info("Registering new user: {}", request.getEmail());

        // Validate email uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ValidationException("Email already registered: " + request.getEmail());
        }

        // Create user entity
        User user = User.builder()
                .id(IdGenerator.generateId())
                .email(request.getEmail())
                .displayName(request.getDisplayName())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getId());

        return userMapper.toDTO(savedUser);
    }

    /**
     * Authenticate user and generate JWT token
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        // Generate JWT token
        String token = jwtService.generateToken(user);

        log.info("User logged in successfully: {}", user.getId());

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationTime())
                .user(userMapper.toDTO(user))
                .build();
    }

    /**
     * Get user by ID
     */
    @Transactional(readOnly = true)
    public Optional<UserDTO> getUserById(String userId) {
        return userRepository.findById(userId)
                .map(userMapper::toDTO);
    }
}
