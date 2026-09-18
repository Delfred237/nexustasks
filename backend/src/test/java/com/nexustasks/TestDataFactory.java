package com.nexustasks;

import com.nexustasks.auth.dto.RegisterRequest;
import com.nexustasks.task.dto.CreateTaskRequest;
import com.nexustasks.task.entity.TaskPriority;
import com.nexustasks.task.entity.TaskStatus;
import com.nexustasks.user.entity.Role;
import com.nexustasks.user.entity.User;

public class TestDataFactory {

    public static User createUser() {
        return User.builder()
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .passwordHash("$2a$10$hashedpassword")
                .role(Role.USER)
                .enabled(true)
                .emailVerified(true)
                .build();
    }

    public static RegisterRequest createRegisterRequest(String email) {
        return new RegisterRequest(
                "Test",
                "User",
                email,
                "Password123!"
        );
    }

    public static CreateTaskRequest createTaskRequest() {
        return new CreateTaskRequest(
                "Test Task",
                "A test task description",
                TaskStatus.TODO,
                TaskPriority.MEDIUM,
                null,
                null
        );
    }
}