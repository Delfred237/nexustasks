package com.nexustasks.task.service;

import com.nexustasks.task.entity.Task;
import com.nexustasks.task.entity.TaskActivity;
import com.nexustasks.task.entity.TaskActivityType;
import com.nexustasks.task.repository.TaskActivityRepository;
import com.nexustasks.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskActivityService {

    private final TaskActivityRepository taskActivityRepository;

    @Transactional
    public void logActivity(Task task, User actor, TaskActivityType type, String description) {
        TaskActivity activity = TaskActivity.builder()
                .task(task)
                .actor(actor)
                .activityType(type)
                .description(description)
                .build();
        taskActivityRepository.save(activity);
    }
}