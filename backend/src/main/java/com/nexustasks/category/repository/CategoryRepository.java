package com.nexustasks.category.repository;

import com.nexustasks.category.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Récupère les catégories non supprimées d'un utilisateur
    Page<Category> findByOwnerIdAndDeletedFalse(Long ownerId, Pageable pageable);

    Optional<Category> findByPublicIdAndDeletedFalse(String publicId);

    // Vérifie si un slug existe déjà pour un utilisateur donné (hors catégorie courante)
    boolean existsByOwnerIdAndSlugAndIdNot(Long ownerId, String slug, Long excludeId);

    boolean existsByOwnerIdAndSlug(Long ownerId, String slug);
}
