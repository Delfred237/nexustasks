package com.nexustasks.auth.dto;

import java.util.UUID;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String publicId,
        String role
) {}