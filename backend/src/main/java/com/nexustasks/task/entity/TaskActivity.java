package com.nexustasks.task.entity;

import com.nexustasks.common.entity.BaseEntity;
import com.nexustasks.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "task_activities")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskActivity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id", nullable = false)
    private User actor;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false, length = 30)
    private TaskActivityType activityType;

    @Column(columnDefinition = "TEXT")
    private String description;
}