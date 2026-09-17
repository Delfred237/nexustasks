package com.nexustasks.auth.dto;

import jakarta.validation.constraints.*;

public record VerifyEmailRequest(
        @NotBlank @Email String email,
        @NotBlank @Pattern(regexp = "\\d{6}", message = "Code must be 6 digits") String code
) {}