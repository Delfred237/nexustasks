package com.nexustasks.task.controller;

import com.nexustasks.security.service.SecurityUserService;
import com.nexustasks.task.dto.CreateTaskRequest;
import com.nexustasks.task.dto.TaskResponse;
import com.nexustasks.task.dto.UpdateTaskRequest;
import com.nexustasks.task.entity.TaskPriority;
import com.nexustasks.task.entity.TaskStatus;
import com.nexustasks.task.service.TaskService;
import com.nexustasks.user.entity.User;
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
public class TaskController {

    private final TaskService taskService;
    private final SecurityUserService securityUserService;

    @GetMapping("/tasks")
    public ResponseEntity<Page<TaskResponse>> getTasks(
            @RequestParam(required = false, defaultValue = "false") boolean includeArchived,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        User currentUser = securityUserService.getCurrentUser();
        return ResponseEntity.ok(taskService.getTasksForUser(currentUser, includeArchived, pageable));
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<TaskResponse> getTask(@PathVariable String publicId) {
        User currentUser = securityUserService.getCurrentUser();
        return ResponseEntity.ok(taskService.getTaskByPublicId(publicId, currentUser));
    }

    @GetMapping
    public ResponseEntity<Page<TaskResponse>> searchTasks(
            @RequestParam(required = false, defaultValue = "false") boolean includeArchived,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) String category, // C'est le publicId de la catégorie
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
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

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody CreateTaskRequest request) {
        User currentUser = securityUserService.getCurrentUser();
        TaskResponse created = taskService.createTask(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{publicId}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable String publicId,
            @Valid @RequestBody UpdateTaskRequest request
    ) {
        User currentUser = securityUserService.getCurrentUser();
        return ResponseEntity.ok(taskService.updateTask(publicId, request, currentUser));
    }

    @PostMapping("/{publicId}/archive")
    public ResponseEntity<TaskResponse> archiveTask(@PathVariable String publicId) {
        User currentUser = securityUserService.getCurrentUser();
        return ResponseEntity.ok(taskService.archiveTask(publicId, currentUser));
    }

    @PostMapping("/{publicId}/restore")
    public ResponseEntity<TaskResponse> restoreTask(@PathVariable String publicId) {
        User currentUser = securityUserService.getCurrentUser();
        return ResponseEntity.ok(taskService.restoreTask(publicId, currentUser));
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<Void> deleteTask(@PathVariable String publicId) {
        User currentUser = securityUserService.getCurrentUser();
        taskService.deleteTask(publicId, currentUser);
        return ResponseEntity.noContent().build();
    }
}