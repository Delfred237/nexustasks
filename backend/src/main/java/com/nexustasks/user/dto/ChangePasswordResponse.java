package com.nexustasks.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Confirmation de changement de mot de passe")
public record ChangePasswordResponse(
        @Schema(description = "Message de confirmation")
        String message,

        @Schema(description = "Indique si tous les autres sessions ont été révoquées")
        boolean otherSessionsRevoked
) {}