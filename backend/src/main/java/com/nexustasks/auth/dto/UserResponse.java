package com.nexustasks.auth.dto;

import com.nexustasks.user.entity.User;

public record UserResponse(
        String publicId,
        String firstName,
        String lastName,
        String email,
        String role,
        boolean emailVerified,
        String avatarUrl
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getPublicId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole().name(),
                user.isEmailVerified(),
                buildAvatarUrl(user)
        );
    }

    /**
     * Convertit le chemin de stockage interne (ex: "avatars/uuid.png")
     * en URL publique servie par FileController (ex: "/api/files/avatars/uuid.png").
     *
     * Sans cette conversion, le frontend recevrait un chemin relatif invalide
     * et l'image ne chargerait pas après un rechargement de page.
     */
    private static String buildAvatarUrl(User user) {
        if (user.getAvatarPath() == null || user.getAvatarPath().isBlank()) {
            return null;
        }
        return "/files/" + user.getAvatarPath();
    }
}