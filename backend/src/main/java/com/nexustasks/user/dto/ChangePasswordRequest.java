package com.nexustasks.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Données de changement de mot de passe")
public record ChangePasswordRequest(
        @Schema(description = "Mot de passe actuel (pour vérification)", example = "OldPassword123!")
        @NotBlank String currentPassword,

        @Schema(
                description = "Nouveau mot de passe (min 8 caractères, majuscule, minuscule, chiffre, caractère spécial)",
                example = "NewPassword456!"
        )
        @NotBlank
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "Password must be 8+ chars with upper, lower, digit and special character"
        ) String newPassword
) {}