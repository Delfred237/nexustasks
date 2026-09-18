package com.nexustasks.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexustasks.integration.BaseIntegrationTest;
import com.nexustasks.security.service.JwtService;
import com.nexustasks.user.dto.ChangePasswordRequest;
import com.nexustasks.user.dto.UpdateProfileRequest;
import com.nexustasks.user.entity.Role;
import com.nexustasks.user.entity.User;
import com.nexustasks.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class UserControllerIT extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private User testUser;
    private String accessToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        testUser = User.builder()
                .firstName("Alice")
                .lastName("Martin")
                .email("alice.profile@example.com")
                .passwordHash(passwordEncoder.encode("OldPassword123!"))
                .role(Role.USER)
                .enabled(true)
                .emailVerified(true)
                .build();
        testUser = userRepository.save(testUser);

        var userDetails = new org.springframework.security.core.userdetails.User(
                testUser.getEmail(),
                testUser.getPasswordHash(),
                java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER"))
        );
        accessToken = jwtService.generateToken(userDetails);
    }

    @Test
    @DisplayName("PATCH /api/users/me - Mettre à jour le profil")
    void updateProfile_WithValidData_ShouldReturn200() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest("Bob", "Smith");

        mockMvc.perform(patch("/api/users/me")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Bob"))
                .andExpect(jsonPath("$.lastName").value("Smith"));
    }

    @Test
    @DisplayName("PATCH /api/users/me/password - Changer le mot de passe")
    void changePassword_WithValidData_ShouldReturn200() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest(
                "OldPassword123!",
                "NewPassword456!"
        );

        mockMvc.perform(patch("/api/users/me/password")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password changed successfully. All other sessions have been revoked."))
                .andExpect(jsonPath("$.otherSessionsRevoked").value(true));
    }

    @Test
    @DisplayName("PATCH /api/users/me/password - Échec avec ancien mot de passe incorrect")
    void changePassword_WithWrongCurrentPassword_ShouldReturn400() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest(
                "WrongPassword!",
                "NewPassword456!"
        );

        mockMvc.perform(patch("/api/users/me/password")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("WRONG_CURRENT_PASSWORD"));
    }
}