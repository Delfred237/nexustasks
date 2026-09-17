package com.nexustasks.category.controller;

import com.nexustasks.category.dto.CategoryResponse;
import com.nexustasks.category.dto.CreateCategoryRequest;
import com.nexustasks.category.dto.UpdateCategoryRequest;
import com.nexustasks.category.service.CategoryService;
import com.nexustasks.security.service.SecurityUserService;
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
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final SecurityUserService securityUserService;

    @GetMapping
    public ResponseEntity<Page<CategoryResponse>> getCategories(
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        User currentUser = securityUserService.getCurrentUser();
        return ResponseEntity.ok(categoryService.getCategoriesForUser(currentUser, pageable));
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<CategoryResponse> getCategory(@PathVariable String publicId) {
        User currentUser = securityUserService.getCurrentUser();
        return ResponseEntity.ok(categoryService.getCategoryByPublicId(publicId, currentUser));
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        User currentUser = securityUserService.getCurrentUser();
        CategoryResponse created = categoryService.createCategory(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{publicId}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable String publicId,
            @Valid @RequestBody UpdateCategoryRequest request
    ) {
        User currentUser = securityUserService.getCurrentUser();
        return ResponseEntity.ok(categoryService.updateCategory(publicId, request, currentUser));
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String publicId) {
        User currentUser = securityUserService.getCurrentUser();
        categoryService.deleteCategory(publicId, currentUser);
        return ResponseEntity.noContent().build();
    }
}
