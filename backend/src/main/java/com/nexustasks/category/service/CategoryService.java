package com.nexustasks.category.service;

import com.nexustasks.category.dto.CategoryResponse;
import com.nexustasks.category.dto.CreateCategoryRequest;
import com.nexustasks.category.dto.UpdateCategoryRequest;
import com.nexustasks.category.entity.Category;
import com.nexustasks.category.repository.CategoryRepository;
import com.nexustasks.common.util.SlugGenerator;
import com.nexustasks.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final SlugGenerator slugGenerator;

    @Transactional(readOnly = true)
    public Page<CategoryResponse> getCategoriesForUser(User user, Pageable pageable) {
        return categoryRepository.findByOwnerIdAndDeletedFalse(user.getId(), pageable)
                .map(CategoryResponse::from);
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryByPublicId(String publicId, User user) {
        Category category = findCategoryByPublicId(publicId);
        checkOwnership(category, user);
        return CategoryResponse.from(category);
    }

    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request, User user) {
        String baseSlug = slugGenerator.toSlug(request.name());
        String uniqueSlug = generateUniqueSlug(baseSlug, user.getId(), null);

        Category category = Category.builder()
                .name(request.name())
                .slug(uniqueSlug)
                .description(request.description())
                .color(request.color() != null ? request.color() : "#6b7280")
                .owner(user)
                .build();

        category = categoryRepository.save(category);
        return CategoryResponse.from(category);
    }

    @Transactional
    public CategoryResponse updateCategory(String publicId, UpdateCategoryRequest request, User user) {
        Category category = findCategoryByPublicId(publicId);
        checkOwnership(category, user);

        // Si le nom change, on doit recalculer le slug et vérifier son unicité
        if (!category.getName().equals(request.name())) {
            String baseSlug = slugGenerator.toSlug(request.name());
            String uniqueSlug = generateUniqueSlug(baseSlug, user.getId(), category.getId());
            category.setSlug(uniqueSlug);
        }

        category.setName(request.name());
        category.setDescription(request.description());
        if (request.color() != null) {
            category.setColor(request.color());
        }

        category = categoryRepository.save(category);
        return CategoryResponse.from(category);
    }

    @Transactional
    public void deleteCategory(String publicId, User user) {
        Category category = findCategoryByPublicId(publicId);
        checkOwnership(category, user);

        // Soft Delete
        category.softDelete();
        categoryRepository.save(category);
    }


    // ==========================================
    // PRIVATE HELPERS
    // ==========================================

    private Category findCategoryByPublicId(String publicId) {
        return categoryRepository.findByPublicIdAndDeletedFalse(publicId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
    }

    /**
     * VÉRIFICATION D'OWNERSHIP (CRITIQUE POUR LA SÉCURITÉ)
     * Empêche un utilisateur d'accéder ou modifier les catégories d'un autre utilisateur.
     */
    private void checkOwnership(Category category, User user) {
        if (!category.getOwner().getId().equals(user.getId())) {
            // On renvoie 404 (Not Found) plutôt que 403 (Forbidden) pour ne pas
            // révéler l'existence de la ressource à un utilisateur non autorisé (Security by Obscurity).
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
        }
    }

    /**
     * Génère un slug unique pour l'utilisateur.
     * Si "work" existe déjà, essaie "work-1", "work-2", etc.
     */
    private String generateUniqueSlug(String baseSlug, Long ownerId, Long excludeId) {
        String slug = baseSlug;
        int counter = 1;

        while (true) {
            boolean exists;
            if (excludeId != null) {
                exists = categoryRepository.existsByOwnerIdAndSlugAndIdNot(ownerId, slug, excludeId);
            } else {
                exists = categoryRepository.existsByOwnerIdAndSlug(ownerId, slug);
            }

            if (!exists) {
                return slug;
            }
            slug = baseSlug + "-" + counter;
            counter++;

            // Sécurité anti-boucle infinie
            if (counter > 100) {
                throw new IllegalStateException("Could not generate unique slug after 100 attempts");
            }
        }
    }

}
