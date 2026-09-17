package com.nexustasks.auth.dto;

import jakarta.validation.constraints.*;

public record ResendVerificationRequest(@NotBlank @Email String email) {}