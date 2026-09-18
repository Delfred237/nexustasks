package com.nexustasks.notification.event;

import com.nexustasks.task.entity.Task;

public class TaskRestoredEvent extends TaskEvent {
    public TaskRestoredEvent(Object source, Task task) {
        super(source, task);
    }
}