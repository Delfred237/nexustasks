package com.nexustasks.user.service;

import com.nexustasks.auth.dto.UserResponse;
import com.nexustasks.auth.repository.RefreshTokenRepository;
import com.nexustasks.common.exception.BusinessException;
import com.nexustasks.common.exception.ErrorCode;
import com.nexustasks.user.dto.ChangePasswordRequest;
import com.nexustasks.user.dto.ChangePasswordResponse;
import com.nexustasks.user.dto.UpdateProfileRequest;
import com.nexustasks.user.entity.Role;
import com.nexustasks.user.entity.User;
import com.nexustasks.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.nexustasks.TestAssertions.hasErrorCode;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .firstName("Alice")
                .lastName("Martin")
                .email("alice@example.com")
                .passwordHash("$2a$10$hashedpassword")
                .role(Role.USER)
                .enabled(true)
                .emailVerified(true)
                .build();
        testUser.setPublicId("test-uuid-123");
    }

    // =====================================================
    // UPDATE PROFILE TESTS
    // =====================================================

    @Test
    @DisplayName("UpdateProfile: succès avec données valides")
    void updateProfile_WithValidData_ShouldUpdateUser() {
        // Given
        UpdateProfileRequest request = new UpdateProfileRequest("Bob", "Smith");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        UserResponse response = userService.updateProfile(testUser, request);

        // Then
        assertThat(response.firstName()).isEqualTo("Bob");
        assertThat(response.lastName()).isEqualTo("Smith");
        verify(userRepository, times(1)).save(testUser);
        verify(eventPublisher, times(1)).publishEvent(any());
    }

    // =====================================================
    // CHANGE PASSWORD TESTS
    // =====================================================

    @Test
    @DisplayName("ChangePassword: succès avec ancien mot de passe correct")
    void changePassword_WithCorrectCurrentPassword_ShouldSucceed() {
        // Given
        ChangePasswordRequest request = new ChangePasswordRequest(
                "OldPassword123!",
                "NewPassword456!"
        );
        when(passwordEncoder.matches("OldPassword123!", testUser.getPasswordHash())).thenReturn(true);
        when(passwordEncoder.matches("NewPassword456!", testUser.getPasswordHash())).thenReturn(false);
        when(passwordEncoder.encode("NewPassword456!")).thenReturn("$2a$10$newhashedpassword");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        ChangePasswordResponse response = userService.changePassword(testUser, request);

        // Then
        assertThat(response.message()).contains("Password changed successfully");
        assertThat(response.otherSessionsRevoked()).isTrue();
        assertThat(testUser.getPasswordHash()).isEqualTo("$2a$10$newhashedpassword");
        verify(refreshTokenRepository, times(1)).deleteByUserId(testUser.getId());
        verify(eventPublisher, times(1)).publishEvent(any());
    }

    @Test
    @DisplayName("ChangePassword: échec avec ancien mot de passe incorrect")
    void changePassword_WithWrongCurrentPassword_ShouldThrowException() {
        // Given
        ChangePasswordRequest request = new ChangePasswordRequest(
                "WrongPassword!",
                "NewPassword456!"
        );
        when(passwordEncoder.matches("WrongPassword!", testUser.getPasswordHash())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> userService.changePassword(testUser, request))
                .isInstanceOf(BusinessException.class)
                .satisfies(hasErrorCode(ErrorCode.WRONG_CURRENT_PASSWORD));

        verify(userRepository, never()).save(any());
        verify(refreshTokenRepository, never()).deleteByUserId(any());
    }

    @Test
    @DisplayName("ChangePassword: échec si le nouveau mot de passe est identique à l'ancien")
    void changePassword_WithSamePassword_ShouldThrowException() {
        // Given
        ChangePasswordRequest request = new ChangePasswordRequest(
                "OldPassword123!",
                "OldPassword123!" // Même que l'ancien
        );
        when(passwordEncoder.matches("OldPassword123!", testUser.getPasswordHash())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.changePassword(testUser, request))
                .isInstanceOf(BusinessException.class)
                .satisfies(hasErrorCode(ErrorCode.PASSWORD_REUSED));

        verify(userRepository, never()).save(any());
    }
}