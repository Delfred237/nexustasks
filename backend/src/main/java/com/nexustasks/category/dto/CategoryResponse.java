package com.nexustasks.category.dto;

import com.nexustasks.category.entity.Category;

import java.time.Instant;

public record CategoryResponse(
        String publicId,
        String name,
        String slug,
        String description,
        String color,
        Instant createdAt,
        Instant updatedAt
) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getPublicId(),
                category.getName(),
                category.getSlug(),
                category.getDescription(),
                category.getColor(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}