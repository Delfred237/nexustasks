package com.nexustasks.task.service;

import com.nexustasks.category.entity.Category;
import com.nexustasks.category.repository.CategoryRepository;
import com.nexustasks.common.util.SlugGenerator;
import com.nexustasks.notification.event.TaskArchivedEvent;
import com.nexustasks.notification.event.TaskCompletedEvent;
import com.nexustasks.notification.event.TaskCreatedEvent;
import com.nexustasks.notification.event.TaskRestoredEvent;
import com.nexustasks.task.dto.CreateTaskRequest;
import com.nexustasks.task.dto.TaskResponse;
import com.nexustasks.task.dto.UpdateTaskRequest;
import com.nexustasks.task.entity.Task;
import com.nexustasks.task.entity.TaskPriority;
import com.nexustasks.task.entity.TaskStatus;
import com.nexustasks.task.repository.TaskRepository;
import com.nexustasks.task.repository.specification.TaskSpecifications;
import com.nexustasks.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;
    private final SlugGenerator slugGenerator;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasksForUser(User user, boolean includeArchived, Pageable pageable) {
        Page<Task> tasks;
        if (includeArchived) {
            tasks = taskRepository.findByOwnerIdAndDeletedFalse(user.getId(), pageable);
        } else {
            tasks = taskRepository.findByOwnerIdAndDeletedFalseAndArchivedFalse(user.getId(), pageable);
        }
        return tasks.map(TaskResponse::from);
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskByPublicId(String publicId, User user) {
        Task task = findTaskByPublicId(publicId);
        checkOwnership(task, user);
        return TaskResponse.from(task);
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> searchTasks(
            User user,
            boolean includeArchived,
            TaskStatus status,
            TaskPriority priority,
            String categoryPublicId,
            String search,
            Pageable pageable
    ) {
        // 1. Spécifications de base (Sécurité + Soft Delete)
        Specification<Task> spec = Specification.where(TaskSpecifications.hasOwner(user.getId()))
                .and(TaskSpecifications.isNotDeleted());

        // 2. Gestion de l'archivage
        if (!includeArchived) {
            spec = spec.and(TaskSpecifications.isArchived(false));
        }

        // 3. Filtres optionnels dynamiques
        spec = spec.and(TaskSpecifications.hasStatus(status))
                .and(TaskSpecifications.hasPriority(priority))
                .and(TaskSpecifications.belongsToCategory(categoryPublicId))
                .and(TaskSpecifications.titleOrDescriptionContains(search));

        // 4. Exécution de la requête dynamique avec pagination et tri
        return taskRepository.findAll(spec, pageable).map(TaskResponse::from);
    }

    @Transactional
    public TaskResponse createTask(CreateTaskRequest request, User user) {
        String baseSlug = slugGenerator.toSlug(request.title());
        String uniqueSlug = generateUniqueSlug(baseSlug, user.getId(), null);

        Category category = null;
        if (request.categoryPublicId() != null && !request.categoryPublicId().isBlank()) {
            category = categoryRepository.findByPublicIdAndDeletedFalse(request.categoryPublicId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category not found"));
            // Ownership check sur la catégorie également
            if (!category.getOwner().getId().equals(user.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot assign category owned by another user");
            }
        }

        Task task = Task.builder()
                .title(request.title())
                .slug(uniqueSlug)
                .description(request.description())
                .status(request.status())
                .priority(request.priority())
                .dueDate(request.dueDate())
                .owner(user)
                .category(category)
                .archived(false)
                .build();

        task = taskRepository.save(task);

        // Publier l'événement
        eventPublisher.publishEvent(new TaskCreatedEvent(this, task));

        return TaskResponse.from(task);
    }

    @Transactional
    public TaskResponse updateTask(String publicId, UpdateTaskRequest request, User user) {
        Task task = findTaskByPublicId(publicId);
        checkOwnership(task, user);

        // Gestion du slug si le titre change
        if (!task.getTitle().equals(request.title())) {
            String baseSlug = slugGenerator.toSlug(request.title());
            String uniqueSlug = generateUniqueSlug(baseSlug, user.getId(), task.getId());
            task.setSlug(uniqueSlug);
        }

        // Gestion de la catégorie
        Category newCategory = null;
        if (request.categoryPublicId() != null && !request.categoryPublicId().isBlank()) {
            newCategory = categoryRepository.findByPublicIdAndDeletedFalse(request.categoryPublicId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category not found"));
            if (!newCategory.getOwner().getId().equals(user.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot assign category owned by another user");
            }
        }

        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(request.status());
        task.setPriority(request.priority());
        task.setDueDate(request.dueDate());
        task.setCategory(newCategory);

        // Détecter si on vient de compléter la tâche
        boolean wasCompleted = task.getStatus() == TaskStatus.COMPLETED;
        task.setStatus(request.status());
        boolean isNowCompleted = request.status() == TaskStatus.COMPLETED;

        task = taskRepository.save(task);

        // Publier l'événement uniquement si on vient de passer à COMPLETED
        if (!wasCompleted && isNowCompleted) {
            eventPublisher.publishEvent(new TaskCompletedEvent(this, task));
        }

        return TaskResponse.from(task);
    }

    @Transactional
    public TaskResponse archiveTask(String publicId, User user) {
        Task task = findTaskByPublicId(publicId);
        checkOwnership(task, user);
        if (!task.isArchived()) {
            task.setArchived(true);
            task = taskRepository.save(task);
            eventPublisher.publishEvent(new TaskArchivedEvent(this, task)); // ← NOUVEAU
        }
        return TaskResponse.from(task);
    }

    @Transactional
    public TaskResponse restoreTask(String publicId, User user) {
        Task task = findTaskByPublicId(publicId);
        checkOwnership(task, user);
        if (task.isArchived()) {
            task.setArchived(false);
            task = taskRepository.save(task);
            eventPublisher.publishEvent(new TaskRestoredEvent(this, task)); // ← NOUVEAU
        }
        return TaskResponse.from(task);
    }

    @Transactional
    public void deleteTask(String publicId, User user) {
        Task task = findTaskByPublicId(publicId);
        checkOwnership(task, user);
        task.softDelete();
        taskRepository.save(task);
    }

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================

    private Task findTaskByPublicId(String publicId) {
        return taskRepository.findByPublicIdAndDeletedFalse(publicId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
    }

    private void checkOwnership(Task task, User user) {
        if (!task.getOwner().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found");
        }
    }

    private String generateUniqueSlug(String baseSlug, Long ownerId, Long excludeId) {
        String slug = baseSlug;
        int counter = 1;

        while (true) {
            boolean exists;
            if (excludeId != null) {
                exists = taskRepository.existsByOwnerIdAndSlugAndIdNot(ownerId, slug, excludeId);
            } else {
                exists = taskRepository.existsByOwnerIdAndSlug(ownerId, slug);
            }

            if (!exists) {
                return slug;
            }
            slug = baseSlug + "-" + counter;
            counter++;

            if (counter > 100) {
                throw new IllegalStateException("Could not generate unique slug after 100 attempts");
            }
        }
    }
}