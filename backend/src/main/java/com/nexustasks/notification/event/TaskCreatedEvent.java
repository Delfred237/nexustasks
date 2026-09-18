package com.nexustasks.notification.event;

import com.nexustasks.task.entity.Task;

public class TaskCreatedEvent extends TaskEvent {
    public TaskCreatedEvent(Object source, Task task) {
        super(source, task);
    }
}