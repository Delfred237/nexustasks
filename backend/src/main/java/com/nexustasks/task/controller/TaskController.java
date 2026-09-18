package com.nexustasks.task.controller;

import com.nexustasks.security.service.SecurityUserService;
import com.nexustasks.task.dto.CreateTaskRequest;
import com.nexustasks.task.dto.TaskActivityResponse;
import com.nexustasks.task.dto.TaskResponse;
import com.nexustasks.task.dto.UpdateTaskRequest;
import com.nexustasks.task.entity.Task;
import com.nexustasks.task.entity.TaskPriority;
import com.nexustasks.task.entity.TaskStatus;
import com.nexustasks.task.repository.TaskActivityRepository;
import com.nexustasks.task.service.TaskService;
import com.nexustasks.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Gestion complète des tâches avec recherche avancée")
public class TaskController {

    private final TaskService taskService;
    private final SecurityUserService securityUserService;
    private final TaskActivityRepository taskActivityRepository;

    @GetMapping("/tasks")
    public ResponseEntity<Page<TaskResponse>> getTasks(
            @RequestParam(required = false, defaultValue = "false") boolean includeArchived,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        User currentUser = securityUserService.getCurrentUser();
        return ResponseEntity.ok(taskService.getTasksForUser(currentUser, includeArchived, pageable));
    }

    @GetMapping("/{publicId}")
    @Operation(summary = "Obtenir une tâche par son ID public")
    public ResponseEntity<TaskResponse> getTask(@PathVariable String publicId) {
        User currentUser = securityUserService.getCurrentUser();
        return ResponseEntity.ok(taskService.getTaskByPublicId(publicId, currentUser));
    }

    @GetMapping
    @Operation(
            summary = "Rechercher et lister les tâches",
            description = "Recherche dynamique multi-critères avec pagination et tri."
    )
    public ResponseEntity<Page<TaskResponse>> searchTasks(
            @Parameter(description = "Inclure les tâches archivées")
            @RequestParam(required = false, defaultValue = "false") boolean includeArchived,
            @Parameter(description = "Filtrer par statut")
            @RequestParam(required = false) TaskStatus status,
            @Parameter(description = "Filtrer par priorité")
            @RequestParam(required = false) TaskPriority priority,
            @Parameter(description = "Filtrer par publicId de catégorie")
            @RequestParam(required = false) String category,
            @Parameter(description = "Recherche textuelle (titre et description)")
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    )  {
        User currentUser = securityUserService.getCurrentUser();
        return ResponseEntity.ok(taskService.searchTasks(
                currentUser,
                includeArchived,
                status,
                priority,
                category,
                search,
                pageable
        ));
    }

    @GetMapping("/{publicId}/activities")
    @Operation(summary = "Historique d'activité d'une tâche")
    public ResponseEntity<Page<TaskActivityResponse>> getTaskActivities(
            @PathVariable String publicId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        User currentUser = securityUserService.getCurrentUser();
        Task task = taskService.getTaskEntityByPublicId(publicId, currentUser); // Méthode à ajouter au service
        return ResponseEntity.ok(
                taskActivityRepository.findByTaskIdAndDeletedFalseOrderByCreatedAtDesc(task.getId(), pageable)
                        .map(TaskActivityResponse::from)
        );
    }

    @PostMapping
    @Operation(summary = "Créer une nouvelle tâche")
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody CreateTaskRequest request) {
        User currentUser = securityUserService.getCurrentUser();
        TaskResponse created = taskService.createTask(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{publicId}")
    @Operation(summary = "Mettre à jour une tâche existante")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable String publicId,
            @Valid @RequestBody UpdateTaskRequest request
    ) {
        User currentUser = securityUserService.getCurrentUser();
        return ResponseEntity.ok(taskService.updateTask(publicId, request, currentUser));
    }

    @PostMapping("/{publicId}/archive")
    @Operation(summary = "Archiver une tâche (masque des listings par défaut)")
    public ResponseEntity<TaskResponse> archiveTask(@PathVariable String publicId) {
        User currentUser = securityUserService.getCurrentUser();
        return ResponseEntity.ok(taskService.archiveTask(publicId, currentUser));
    }

    @PostMapping("/{publicId}/restore")
    @Operation(summary = "Restaurer une tâche archivée")
    public ResponseEntity<TaskResponse> restoreTask(@PathVariable String publicId) {
        User currentUser = securityUserService.getCurrentUser();
        return ResponseEntity.ok(taskService.restoreTask(publicId, currentUser));
    }

    @DeleteMapping("/{publicId}")
    @Operation(summary = "Supprimer définitivement une tâche (soft delete)")
    public ResponseEntity<Void> deleteTask(@PathVariable String publicId) {
        User currentUser = securityUserService.getCurrentUser();
        taskService.deleteTask(publicId, currentUser);
        return ResponseEntity.noContent().build();
    }
}