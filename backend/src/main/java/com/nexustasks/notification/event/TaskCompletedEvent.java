package com.nexustasks.notification.event;

import com.nexustasks.task.entity.Task;

public class TaskCompletedEvent extends TaskEvent {
    public TaskCompletedEvent(Object source, Task task) {
        super(source, task);
    }
}