package com.nexustasks.auth.service;

import com.nexustasks.auth.config.OtpProperties;
import com.nexustasks.auth.dto.RegisterRequest;
import com.nexustasks.auth.dto.UserResponse;
import com.nexustasks.auth.entity.VerificationCode;
import com.nexustasks.auth.repository.RefreshTokenRepository;
import com.nexustasks.auth.repository.VerificationCodeRepository;
import com.nexustasks.common.exception.BusinessException;
import com.nexustasks.common.exception.ErrorCode;
import com.nexustasks.notification.service.EmailService;
import com.nexustasks.security.service.JwtService;
import com.nexustasks.user.entity.User;
import com.nexustasks.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private VerificationCodeRepository verificationCodeRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private OtpProperties otpProperties;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new RegisterRequest(
                "Alice",
                "Martin",
                "alice@example.com",
                "Password123!"
        );
    }

    @Test
    @DisplayName("Register: succès avec un email unique")
    void register_WithUniqueEmail_ShouldCreateUser() {
        // Given
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Password123!")).thenReturn("$2a$10$hashedpassword");
        when(otpProperties.ttlMinutes()).thenReturn(15);
        when(verificationCodeRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        UserResponse response = authService.register(validRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.email()).isEqualTo("alice@example.com");
        assertThat(response.emailVerified()).isFalse();
        verify(emailService, times(1)).sendVerificationEmail(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Register: échec si l'email existe déjà")
    void register_WithExistingEmail_ShouldThrowConflict() {
        // Given
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authService.register(validRequest))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException bex = (BusinessException) ex;
                    assertThat(bex.getErrorCode()).isEqualTo(ErrorCode.EMAIL_ALREADY_EXISTS);
                    assertThat(bex.getErrorCode().getHttpStatus()).isEqualTo(409);
                });

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("VerifyEmail: succès avec le bon code")
    void verifyEmail_WithCorrectCode_ShouldEnableUser() {
        // Given
        User user = new User();
        user.setEmail("alice@example.com");
        user.setEmailVerified(false);

        VerificationCode code = new VerificationCode();
        code.setCodeHash("a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3"); // SHA-256 de "123"
        code.setAttempts(0);
        code.setExpiresAt(java.time.Instant.now().plusSeconds(900));

        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
        when(verificationCodeRepository.findFirstByUserIdAndUsedFalseOrderByCreatedAtDesc(any()))
                .thenReturn(Optional.of(code));
        when(otpProperties.maxAttempts()).thenReturn(5);

        // When
        authService.verifyEmail(new com.nexustasks.auth.dto.VerifyEmailRequest("alice@example.com", "123"));

        // Then
        assertThat(user.isEmailVerified()).isTrue();
        assertThat(user.isEnabled()).isTrue();
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("VerifyEmail: échec avec un mauvais code (incrémente les tentatives)")
    void verifyEmail_WithWrongCode_ShouldIncrementAttempts() {
        // Given
        User user = new User();
        user.setEmail("alice@example.com");

        VerificationCode code = new VerificationCode();
        code.setCodeHash("correct_hash");
        code.setAttempts(0);
        code.setExpiresAt(java.time.Instant.now().plusSeconds(900));

        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
        when(verificationCodeRepository.findFirstByUserIdAndUsedFalseOrderByCreatedAtDesc(any()))
                .thenReturn(Optional.of(code));
        when(otpProperties.maxAttempts()).thenReturn(5);

        // When & Then
        assertThatThrownBy(() -> authService.verifyEmail(
                new com.nexustasks.auth.dto.VerifyEmailRequest("alice@example.com", "999")
        ))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException bex = (BusinessException) ex;
                    assertThat(bex.getErrorCode()).isEqualTo(ErrorCode.INVALID_OTP);
                });

        assertThat(code.getAttempts()).isEqualTo(1);
        verify(verificationCodeRepository, times(1)).save(code);
    }
}