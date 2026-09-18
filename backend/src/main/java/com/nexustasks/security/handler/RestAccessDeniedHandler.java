package com.nexustasks.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexustasks.common.dto.ApiError;
import com.nexustasks.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Handler pour les requêtes authentifiées mais non autorisées (403 Forbidden).
 *
 * Déclenché quand un utilisateur authentifié tente d'accéder à une ressource
 * sans avoir les permissions nécessaires (ex: rôle insuffisant, ownership non respecté).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private static final String PROBLEM_TYPE_BASE = "https://nexustasks.com/errors/";
    private final ObjectMapper objectMapper;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {
        log.warn("Access denied for user on {} - Reason: {}",
                request.getRequestURI(),
                accessDeniedException.getMessage());

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiError error = ApiError.builder()
                .type(PROBLEM_TYPE_BASE + "access-denied")
                .title("Access denied")
                .status(ErrorCode.ACCESS_DENIED.getHttpStatus())
                .detail("You do not have permission to access this resource.")
                .errorCode(ErrorCode.ACCESS_DENIED.name())
                .timestamp(Instant.now())
                .instance(request.getRequestURI())
                .build();

        objectMapper.writeValue(response.getOutputStream(), error);
    }
}