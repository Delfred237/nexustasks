package com.nexustasks.task.dto;

import com.nexustasks.category.dto.CategoryResponse;
import com.nexustasks.task.entity.Task;
import com.nexustasks.task.entity.TaskPriority;
import com.nexustasks.task.entity.TaskStatus;

import java.time.Instant;

public record TaskResponse(
        String publicId,
        String title,
        String slug,
        String description,
        TaskStatus status,
        TaskPriority priority,
        Instant dueDate,
        Instant completedAt,
        boolean archived,
        Instant archivedAt,
        CategoryResponse category, // Peut être null
        Instant createdAt,
        Instant updatedAt
) {
    public static TaskResponse from(Task task) {
        CategoryResponse categoryResp = null;
        if (task.getCategory() != null && !task.getCategory().isDeleted()) {
            categoryResp = CategoryResponse.from(task.getCategory());
        }

        return new TaskResponse(
                task.getPublicId(),
                task.getTitle(),
                task.getSlug(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                task.getCompletedAt(),
                task.isArchived(),
                task.getArchivedAt(),
                categoryResp,
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}