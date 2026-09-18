package com.nexustasks.common.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Gestionnaire global des exceptions pour les controllers REST.
 * Transforme les exceptions Java en réponses JSON structurées.
 *
 * Pour les erreurs d'authentification, on utilise un handler dédié
 * plutôt que AuthenticationEntryPoint pour avoir plus de granularité
 * sur le type d'erreur (disabled, locked, bad credentials).
 */
@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper;

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Map<String, Object>> handleDisabledException(
            DisabledException ex, HttpServletRequest request) {
        return buildAuthErrorResponse(
                HttpStatus.FORBIDDEN,
                "Account not activated",
                "Your account is not yet activated. Please verify your email address.",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<Map<String, Object>> handleLockedException(
            LockedException ex, HttpServletRequest request) {
        return buildAuthErrorResponse(
                HttpStatus.LOCKED,
                "Account locked",
                "Your account has been locked due to too many failed attempts.",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(
            BadCredentialsException ex, HttpServletRequest request) {
        // Message générique pour ne pas révéler si l'email existe ou non
        return buildAuthErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Invalid credentials",
                "The email or password you entered is incorrect.",
                request.getRequestURI()
        );
    }

    private ResponseEntity<Map<String, Object>> buildAuthErrorResponse(
            HttpStatus status, String error, String message, String path) {

        log.warn("Authentication error on {} - {}: {}", path, error, message);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        body.put("path", path);

        return ResponseEntity.status(status).body(body);
    }
}