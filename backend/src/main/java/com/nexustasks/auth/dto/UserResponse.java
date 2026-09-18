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
                user.getAvatarPath()
        );
    }
}