package com.nexustasks.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record RegisterRequest(
        @Schema(description = "Prénom", example = "Alice", maxLength = 50)
        @NotBlank @Size(max = 50) String firstName,

        @Schema(description = "Nom", example = "Martin", maxLength = 50)
        @NotBlank @Size(max = 50) String lastName,

        @Schema(description = "Adresse email (unique)", example = "alice@example.com", maxLength = 100)
        @NotBlank @Email @Size(max = 100) String email,

        @Schema(
                description = "Mot de passe sécurisé (min 8 caractères, majuscule, minuscule, chiffre, caractère spécial)",
                example = "Password123!"
        )@NotBlank
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "Password must be 8+ chars with upper, lower, digit and special character"
        ) String password
) {}