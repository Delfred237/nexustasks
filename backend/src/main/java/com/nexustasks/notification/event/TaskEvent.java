package com.nexustasks.notification.event;

import com.nexustasks.task.entity.Task;
import org.springframework.context.ApplicationEvent;

public abstract class TaskEvent extends ApplicationEvent {
    private final Task task;

    public TaskEvent(Object source, Task task) {
        super(source);
        this.task = task;
    }

    public Task getTask() {
        return task;
    }
}