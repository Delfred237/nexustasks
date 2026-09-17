package com.nexustasks.task.entity;

import com.nexustasks.category.entity.Category;
import com.nexustasks.common.entity.BaseEntity;
import com.nexustasks.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private TaskStatus status = TaskStatus.TODO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private TaskPriority priority = TaskPriority.MEDIUM;

    @Column(name = "due_date")
    private Instant dueDate;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(nullable = false)
    @Builder.Default
    private boolean archived = false;

    @Column(name = "archived_at")
    private Instant archivedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    /**
     * Callback JPA pour maintenir la cohérence des dates d'état.
     */
    @PreUpdate
    @PrePersist
    public void updateStateTimestamps() {
        // Gérer completedAt
        if (this.status == TaskStatus.COMPLETED && this.completedAt == null) {
            this.completedAt = Instant.now();
        } else if (this.status != TaskStatus.COMPLETED) {
            this.completedAt = null; // Reset si on repasse à TODO/IN_PROGRESS
        }

        // Gérer archivedAt (booléen métier 'archived')
        if (this.archived && this.archivedAt == null) {
            this.archivedAt = Instant.now();
        } else if (!this.archived) {
            this.archivedAt = null; // Reset si désarchivé
        }
    }

}
