package com.nexustasks.task.repository.specification;

import com.nexustasks.category.entity.Category;
import com.nexustasks.task.entity.Task;
import com.nexustasks.task.entity.TaskPriority;
import com.nexustasks.task.entity.TaskStatus;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

public final class TaskSpecifications {

    private TaskSpecifications() {
        // Classe utilitaire, pas d'instanciation
    }

    /**
     * Filtre de sécurité OBLIGATOIRE : la tâche doit appartenir à l'utilisateur.
     */
    public static Specification<Task> hasOwner(Long ownerId) {
        return (root, query, cb) -> cb.equal(root.get("owner").get("id"), ownerId);
    }

    /**
     * Filtre de sécurité OBLIGATOIRE : la tâche ne doit pas être soft-deleted.
     */
    public static Specification<Task> isNotDeleted() {
        return (root, query, cb) -> cb.isFalse(root.get("deleted"));
    }

    public static Specification<Task> hasStatus(TaskStatus status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Task> hasPriority(TaskPriority priority) {
        return (root, query, cb) -> priority == null ? null : cb.equal(root.get("priority"), priority);
    }

    public static Specification<Task> isArchived(boolean archived) {
        return (root, query, cb) -> cb.equal(root.get("archived"), archived);
    }

    /**
     * Filtre par catégorie via son publicId.
     * Nécessite une jointure avec la table Category.
     */
    public static Specification<Task> belongsToCategory(String categoryPublicId) {
        return (root, query, cb) -> {
            if (categoryPublicId == null || categoryPublicId.isBlank()) {
                return null;
            }
            Join<Task, Category> categoryJoin = root.join("category", JoinType.INNER);
            return cb.equal(categoryJoin.get("publicId"), categoryPublicId);
        };
    }

    /**
     * Recherche textuelle insensible à la casse sur le titre et la description.
     */
    public static Specification<Task> titleOrDescriptionContains(String searchText) {
        return (root, query, cb) -> {
            if (searchText == null || searchText.isBlank()) {
                return null;
            }
            String pattern = "%" + searchText.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)
            );
        };
    }
}