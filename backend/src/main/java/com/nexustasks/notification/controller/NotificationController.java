package com.nexustasks.notification.controller;

import com.nexustasks.notification.dto.NotificationResponse;
import com.nexustasks.notification.service.NotificationService;
import com.nexustasks.security.service.SecurityUserService;
import com.nexustasks.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final SecurityUserService securityUserService;

    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> getNotifications(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        User currentUser = securityUserService.getCurrentUser();
        return ResponseEntity.ok(notificationService.getNotificationsForUser(currentUser, pageable));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount() {
        User currentUser = securityUserService.getCurrentUser();
        return ResponseEntity.ok(notificationService.getUnreadCount(currentUser));
    }

    @PatchMapping("/{publicId}/read")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable String publicId) {
        User currentUser = securityUserService.getCurrentUser();
        return ResponseEntity.ok(notificationService.markAsRead(publicId, currentUser));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead() {
        User currentUser = securityUserService.getCurrentUser();
        notificationService.markAllAsRead(currentUser);
        return ResponseEntity.noContent().build();
    }
}