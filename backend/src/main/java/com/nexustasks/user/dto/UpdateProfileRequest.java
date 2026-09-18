package com.nexustasks.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Données de mise à jour du profil utilisateur")
public record UpdateProfileRequest(
        @Schema(description = "Prénom", example = "Alice", maxLength = 50)
        @NotBlank @Size(max = 50) String firstName,

        @Schema(description = "Nom", example = "Martin", maxLength = 50)
        @NotBlank @Size(max = 50) String lastName
) {}