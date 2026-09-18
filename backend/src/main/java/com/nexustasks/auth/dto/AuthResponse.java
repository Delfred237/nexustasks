package com.nexustasks.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "Réponse d'authentification avec tokens")
public record AuthResponse(
        @Schema(description = "Access Token JWT (courte durée, ~15min)")
        String accessToken,

        @Schema(description = "Refresh Token (longue durée, ~7 jours)")
        String refreshToken,

        @Schema(description = "ID public de l'utilisateur (UUID)")
        String publicId,

        @Schema(description = "Rôle de l'utilisateur", example = "USER")
        String role
) {}