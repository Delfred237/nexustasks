package com.nexustasks.task.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexustasks.integration.BaseIntegrationTest;
import com.nexustasks.task.dto.CreateTaskRequest;
import com.nexustasks.task.entity.TaskPriority;
import com.nexustasks.task.entity.TaskStatus;
import com.nexustasks.user.entity.Role;
import com.nexustasks.user.entity.User;
import com.nexustasks.user.repository.UserRepository;
import com.nexustasks.security.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class TaskOwnershipIT extends BaseIntegrationTest {

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

    private User userA;
    private User userB;
    private String tokenA;
    private String tokenB;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        // Créer deux utilisateurs
        userA = createUser("usera@example.com");
        userB = createUser("userb@example.com");

        tokenA = generateToken(userA);
        tokenB = generateToken(userB);
    }

    private User createUser(String email) {
        return userRepository.save(User.builder()
                .firstName("User")
                .lastName(email.substring(0, 1).toUpperCase())
                .email(email)
                .passwordHash(passwordEncoder.encode("Password123!"))
                .role(Role.USER)
                .enabled(true)
                .emailVerified(true)
                .build());
    }

    private String generateToken(User user) {
        var userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER"))
        );
        return jwtService.generateToken(userDetails);
    }

    @Test
    @DisplayName("Sécurité: User B ne peut pas accéder à la tâche de User A")
    void userB_CannotAccess_UserATask() throws Exception {
        // User A crée une tâche
        CreateTaskRequest request = new CreateTaskRequest(
                "Tâche privée de A",
                "Description",
                TaskStatus.TODO,
                TaskPriority.MEDIUM,
                null,
                null
        );

        MvcResult result = mockMvc.perform(post("/tasks")
                        .header("Authorization", "Bearer " + tokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        String taskPublicId = objectMapper.readTree(responseBody).get("publicId").asText();

        // User B tente d'accéder à la tâche de User A
        mockMvc.perform(get("/tasks/" + taskPublicId)
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isNotFound()); // 404 pour ne pas révéler l'existence
    }

    @Test
    @DisplayName("Sécurité: User B ne peut pas supprimer la tâche de User A")
    void userB_CannotDelete_UserATask() throws Exception {
        // User A crée une tâche
        CreateTaskRequest request = new CreateTaskRequest(
                "Tâche privée de A",
                "Description",
                TaskStatus.TODO,
                TaskPriority.MEDIUM,
                null,
                null
        );

        MvcResult result = mockMvc.perform(post("/tasks")
                        .header("Authorization", "Bearer " + tokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String taskPublicId = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("publicId").asText();

        // User B tente de supprimer la tâche de User A
        mockMvc.perform(delete("/tasks/" + taskPublicId)
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isNotFound());
    }
}