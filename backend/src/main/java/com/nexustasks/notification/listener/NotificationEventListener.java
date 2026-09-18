package com.nexustasks.notification.listener;

import com.nexustasks.notification.entity.Notification;
import com.nexustasks.notification.entity.NotificationType;
import com.nexustasks.notification.event.*;
import com.nexustasks.notification.repository.NotificationRepository;
import com.nexustasks.task.entity.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final NotificationRepository notificationRepository;

    @EventListener
    public void onTaskCreated(TaskCreatedEvent event) {
        Task task = event.getTask();
        log.info("TaskCreatedEvent received for task: {}", task.getPublicId());

        Notification notification = Notification.builder()
                .recipient(task.getOwner())
                .type(NotificationType.TASK_CREATED)
                .title("Nouvelle tâche créée")
                .message("La tâche \"" + task.getTitle() + "\" a été créée avec succès.")
                .resourcePublicId(task.getPublicId())
                .build();

        notificationRepository.save(notification);
    }

    @EventListener
    public void onTaskCompleted(TaskCompletedEvent event) {
        Task task = event.getTask();
        log.info("TaskCompletedEvent received for task: {}", task.getPublicId());

        Notification notification = Notification.builder()
                .recipient(task.getOwner())
                .type(NotificationType.TASK_COMPLETED)
                .title("Tâche terminée")
                .message("Félicitations ! La tâche \"" + task.getTitle() + "\" est marquée comme terminée.")
                .resourcePublicId(task.getPublicId())
                .build();

        notificationRepository.save(notification);
    }

    @EventListener
    public void onTaskArchived(TaskArchivedEvent event) {
        Task task = event.getTask();
        log.info("TaskArchivedEvent received for task: {}", task.getPublicId());

        Notification notification = Notification.builder()
                .recipient(task.getOwner())
                .type(NotificationType.TASK_ARCHIVED)
                .title("Tâche archivée")
                .message("La tâche \"" + task.getTitle() + "\" a été archivée.")
                .resourcePublicId(task.getPublicId())
                .build();

        notificationRepository.save(notification);
    }

    @EventListener
    public void onTaskRestored(TaskRestoredEvent event) {
        Task task = event.getTask();
        log.info("TaskRestoredEvent received for task: {}", task.getPublicId());

        Notification notification = Notification.builder()
                .recipient(task.getOwner())
                .type(NotificationType.TASK_RESTORED)
                .title("Tâche restaurée")
                .message("La tâche \"" + task.getTitle() + "\" a été restaurée.")
                .resourcePublicId(task.getPublicId())
                .build();

        notificationRepository.save(notification);
    }
}