package com.nexustasks.category.dto;

import jakarta.validation.constraints.*;

public record UpdateCategoryRequest(
        @NotBlank @Size(max = 100) String name,
        @Size(max = 500) String description,
        @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "Invalid hex color format")
        @Size(max = 7) String color
) {}