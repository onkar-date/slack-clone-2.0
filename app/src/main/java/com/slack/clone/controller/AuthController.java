package com.slack.clone.controller;

import com.slack.clone.identity.dto.AuthResponse;
import com.slack.clone.identity.dto.LoginRequest;
import com.slack.clone.identity.dto.RegisterRequest;
import com.slack.clone.identity.dto.UserDTO;
import com.slack.clone.identity.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication and user management
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and user management APIs")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody RegisterRequest request) {
        UserDTO user = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and get JWT token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<UserDTO> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        return authService.getUserById(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
