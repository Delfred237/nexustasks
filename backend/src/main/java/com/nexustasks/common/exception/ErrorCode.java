package com.nexustasks.common.exception;

/**
 * Catalogue des codes d'erreur métier de l'application.
 *
 * Chaque code correspond à un scénario d'erreur spécifique que le frontend
 * peut utiliser pour afficher un message localisé ou déclencher une action.
 *
 * Convention de nommage : ENTITY_ACTION_REASON (ex: EMAIL_ALREADY_EXISTS)
 */
public enum ErrorCode {

    // ============ AUTHENTICATION ============
    INVALID_CREDENTIALS(401, "Invalid email or password"),
    TOKEN_EXPIRED(401, "Authentication token has expired"),
    TOKEN_INVALID(401, "Authentication token is invalid"),
    TOKEN_REVOKED(401, "Session compromised, all tokens revoked"),
    REFRESH_TOKEN_EXPIRED(401, "Refresh token expired"),
    REFRESH_TOKEN_NOT_FOUND(401, "Missing refresh token"),
    INVALID_REFRESH_TOKEN(401, "Invalid refresh token"),
    EMAIL_NOT_VERIFIED(403, "Email address is not verified"),
    ACCOUNT_DISABLED(403, "Account is disabled"),
    ACCOUNT_LOCKED(423, "Account is locked due to too many failed attempts"),
    SESSION_COMPROMISED(401, "Session has been revoked due to suspicious activity"),

    // ============ AUTHORIZATION ============
    ACCESS_DENIED(403, "You do not have permission to perform this action"),
    RESOURCE_NOT_OWNER(403, "You are not the owner of this resource"),

    // ============ VALIDATION ============
    VALIDATION_ERROR(400, "Request validation failed"),
    INVALID_OTP(400, "Invalid verification code"),
    OTP_EXPIRED(410, "Verification code has expired"),
    OTP_ACTIVE_NOT_FOUND(400, "No active verification code"),
    OTP_MAX_ATTEMPTS(429, "Too many verification attempts"),
    OTP_RESEND_COOLDOWN(429, "Please wait before requesting a new code"),
    EMAIL_ALREADY_EXISTS(409, "An account with this email already exists"),
    EMAIL_ALREADY_VERIFIED(400, "Email is already verified"),
    PASSWORD_TOO_WEAK(400, "Password does not meet security requirements"),

    // ============ RESOURCES ============
    RESOURCE_NOT_FOUND(404, "The requested resource was not found"),
    USER_NOT_FOUND(404, "User not found"),
    RESOURCE_CONFLICT(409, "The resource could not be created due to a conflict"),
    RESOURCE_VERSION_CONFLICT(409, "The resource has been modified by another user"),

    // ============ FILE UPLOAD ============
    FILE_EMPTY(400, "Uploaded file is empty"),
    FILE_TOO_LARGE(413, "File exceeds the maximum allowed size"),
    FILE_INVALID_TYPE(415, "File type is not supported"),
    FILE_STORAGE_ERROR(500, "Failed to process the uploaded file"),

    // ============ RATE LIMITING ============
    RATE_LIMIT_EXCEEDED(429, "Too many requests, please slow down"),

    // ============ INTERNAL ============
    INTERNAL_ERROR(500, "An unexpected error occurred"),
    SERVICE_UNAVAILABLE(503, "The service is temporarily unavailable");

    private final int httpStatus;
    private final String defaultMessage;

    ErrorCode(int httpStatus, String defaultMessage) {
        this.httpStatus = httpStatus;
        this.defaultMessage = defaultMessage;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}