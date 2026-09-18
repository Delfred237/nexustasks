package com.nexustasks.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @Schema(description = "Email de l'utilisateur", example = "alice@example.com")
        @NotBlank @Email String email,

        @Schema(description = "Mot de passe", example = "Password123!")
        @NotBlank String password
) {}