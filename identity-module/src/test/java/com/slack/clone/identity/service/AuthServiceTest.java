package com.slack.clone.identity.service;

import com.slack.clone.identity.dto.LoginRequest;
import com.slack.clone.identity.dto.RegisterRequest;
import com.slack.clone.identity.dto.UserDTO;
import com.slack.clone.identity.entity.User;
import com.slack.clone.identity.mapper.UserMapper;
import com.slack.clone.identity.repository.UserRepository;
import com.slack.clone.identity.security.JwtService;
import com.slack.clone.shared.exception.UnauthorizedException;
import com.slack.clone.shared.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthService
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private User user;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setDisplayName("Test User");

        user = User.builder()
                .id("user-123")
                .email("test@example.com")
                .displayName("Test User")
                .passwordHash("hashed-password")
                .build();

        userDTO = UserDTO.builder()
                .id("user-123")
                .email("test@example.com")
                .displayName("Test User")
                .build();
    }

    @Test
    @Disabled
    void shouldRegisterNewUser() {
        // Given
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        // When
        UserDTO result = authService.register(registerRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(registerRequest.getEmail());
        assertThat(result.getDisplayName()).isEqualTo(registerRequest.getDisplayName());

        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(passwordEncoder).encode(registerRequest.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @Disabled
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Given
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Email already registered");

        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @Disabled
    void shouldLoginSuccessfully() {
        // Given
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password123");
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");
        when(jwtService.getExpirationTime()).thenReturn(3600L);
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        // When
        var result = authService.login(loginRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("jwt-token");
        assertThat(result.getTokenType()).isEqualTo("Bearer");
        assertThat(result.getUser()).isEqualTo(userDTO);

        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder).matches(loginRequest.getPassword(), user.getPasswordHash());
        verify(jwtService).generateToken(user);
    }

    @Test
    @Disabled
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        LoginRequest loginRequest = new LoginRequest("wrong@example.com", "password123");
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Invalid credentials");

        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    @Disabled
    void shouldThrowExceptionWhenPasswordIsWrong() {
        // Given
        LoginRequest loginRequest = new LoginRequest("test@example.com", "wrongpassword");
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Invalid credentials");

        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder).matches(loginRequest.getPassword(), user.getPasswordHash());
        verify(jwtService, never()).generateToken(any());
    }
}
