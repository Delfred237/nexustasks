package com.nexustasks.task.repository;

import com.nexustasks.task.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // Récupère les tâches non supprimées d'un utilisateur (filtrage de base)
    Page<Task> findByOwnerIdAndDeletedFalse(Long ownerId, Pageable pageable);

    // Récupère les tâches non supprimées ET non archivées (listing par défaut)
    Page<Task> findByOwnerIdAndDeletedFalseAndArchivedFalse(Long ownerId, Pageable pageable);

    Optional<Task> findByPublicIdAndDeletedFalse(String publicId);

    // Vérification d'unicité du slug par utilisateur
    boolean existsByOwnerIdAndSlugAndIdNot(Long ownerId, String slug, Long excludeId);

    boolean existsByOwnerIdAndSlug(Long ownerId, String slug);

    // Comptages pour le dashboard
    long countByOwnerIdAndDeletedFalseAndArchivedFalseAndStatus(Long ownerId, com.nexustasks.task.entity.TaskStatus status);
}