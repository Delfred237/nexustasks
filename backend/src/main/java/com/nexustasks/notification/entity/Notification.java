package com.nexustasks.notification.entity;

import com.nexustasks.common.entity.BaseEntity;
import com.nexustasks.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NotificationType type;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private boolean read = false;

    @Column(name = "read_at")
    private Instant readAt;

    @Column(name = "resource_public_id")
    private String resourcePublicId; // Lien vers la tâche/catégorie concernée

    /**
     * Callback pour maintenir la cohérence de readAt.
     */
    @PreUpdate
    public void updateReadTimestamp() {
        if (this.read && this.readAt == null) {
            this.readAt = Instant.now();
        } else if (!this.read) {
            this.readAt = null;
        }
    }
}