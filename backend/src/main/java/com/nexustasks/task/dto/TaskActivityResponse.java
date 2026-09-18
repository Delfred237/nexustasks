package com.nexustasks.task.dto;

import com.nexustasks.task.entity.TaskActivity;
import com.nexustasks.task.entity.TaskActivityType;

import java.time.Instant;

public record TaskActivityResponse(
        String publicId,
        TaskActivityType activityType,
        String description,
        String actorEmail,
        Instant createdAt
) {
    public static TaskActivityResponse from(TaskActivity activity) {
        return new TaskActivityResponse(
                activity.getPublicId(),
                activity.getActivityType(),
                activity.getDescription(),
                activity.getActor().getEmail(),
                activity.getCreatedAt()
        );
    }
}