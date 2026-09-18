package com.nexustasks.notification.dto;

import com.nexustasks.notification.entity.Notification;
import com.nexustasks.notification.entity.NotificationType;

import java.time.Instant;

public record NotificationResponse(
        String publicId,
        NotificationType type,
        String title,
        String message,
        boolean read,
        Instant readAt,
        String resourcePublicId,
        Instant createdAt
) {
    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.getPublicId(),
                notification.getType(),
                notification.getTitle(),
                notification.getMessage(),
                notification.isRead(),
                notification.getReadAt(),
                notification.getResourcePublicId(),
                notification.getCreatedAt()
        );
    }
}