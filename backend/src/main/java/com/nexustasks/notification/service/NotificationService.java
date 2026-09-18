package com.nexustasks.notification.service;

import com.nexustasks.notification.dto.NotificationResponse;
import com.nexustasks.notification.repository.NotificationRepository;
import com.nexustasks.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public Page<NotificationResponse> getNotificationsForUser(User user, Pageable pageable) {
        return notificationRepository.findByRecipientIdAndDeletedFalseOrderByCreatedAtDesc(user.getId(), pageable)
                .map(NotificationResponse::from);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(User user) {
        return notificationRepository.countByRecipientIdAndDeletedFalseAndReadFalse(user.getId());
    }

    @Transactional
    public NotificationResponse markAsRead(String publicId, User user) {
        var notification = notificationRepository.findByPublicIdAndDeletedFalse(publicId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));

        // Vérification d'ownership
        if (!notification.getRecipient().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found");
        }

        notification.setRead(true);
        notification = notificationRepository.save(notification);
        return NotificationResponse.from(notification);
    }

    @Transactional
    public void markAllAsRead(User user) {
        notificationRepository.markAllAsRead(user.getId());
    }
}