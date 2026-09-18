package com.nexustasks.notification.event;

import com.nexustasks.task.entity.Task;

public class TaskArchivedEvent extends TaskEvent {
    public TaskArchivedEvent(Object source, Task task) {
        super(source, task);
    }
}