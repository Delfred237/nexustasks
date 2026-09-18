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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class TaskControllerIT extends BaseIntegrationTest {

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
        // Nettoyer la base avant chaque test
        userRepository.deleteAll();

        // Créer un utilisateur de test vérifié
        testUser = User.builder()
                .firstName("Test")
                .lastName("User")
                .email("test.user@example.com")
                .passwordHash(passwordEncoder.encode("Password123!"))
                .role(Role.USER)
                .enabled(true)
                .emailVerified(true)
                .build();
        testUser = userRepository.save(testUser);

        // Générer un token JWT pour cet utilisateur
        var userDetails = new org.springframework.security.core.userdetails.User(
                testUser.getEmail(),
                testUser.getPasswordHash(),
                java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER"))
        );
        accessToken = jwtService.generateToken(userDetails);
    }

    @Test
    @DisplayName("POST /tasks - succès avec authentification")
    void createTask_WithValidToken_ShouldReturn201() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest(
                "Ma première tâche",
                "Description de la tâche",
                TaskStatus.TODO,
                TaskPriority.MEDIUM,
                null,
                null
        );

        mockMvc.perform(post("/tasks")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Ma première tâche"))
                .andExpect(jsonPath("$.status").value("TODO"));
    }

    @Test
    @DisplayName("POST /tasks - échec sans authentification")
    void createTask_WithoutToken_ShouldReturn401() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest(
                "Ma tâche",
                "Description",
                TaskStatus.TODO,
                TaskPriority.MEDIUM,
                null,
                null
        );

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /tasks - retourne uniquement les tâches de l'utilisateur")
    void getTasks_ShouldReturnOnlyUserTasks() throws Exception {
        // Créer une tâche pour l'utilisateur
        CreateTaskRequest request = new CreateTaskRequest(
                "Ma tâche",
                "Description",
                TaskStatus.TODO,
                TaskPriority.MEDIUM,
                null,
                null
        );

        mockMvc.perform(post("/tasks")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Vérifier que la liste contient bien la tâche
        mockMvc.perform(get("/tasks")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Ma tâche"));
    }
}