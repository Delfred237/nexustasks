package com.nexustasks.task.dto;

import com.nexustasks.task.entity.TaskPriority;
import com.nexustasks.task.entity.TaskStatus;
import jakarta.validation.constraints.*;

import java.time.Instant;

public record UpdateTaskRequest(
        @NotBlank @Size(max = 255) String title,
        @Size(max = 5000) String description,
        @NotNull TaskStatus status,
        @NotNull TaskPriority priority,
        Instant dueDate,
        String categoryPublicId // Optionnel : ID public de la catégorie, ou null pour retirer
) {}