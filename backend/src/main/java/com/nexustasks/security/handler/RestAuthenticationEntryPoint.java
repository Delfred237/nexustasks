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
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Point d'entrée pour les requêtes non authentifiées (401 Unauthorized).
 *
 * Déclenché quand une requête atteint un endpoint protégé sans token valide.
 * Retourne une réponse JSON structurée au lieu de la page HTML par défaut de Spring Security.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final String PROBLEM_TYPE_BASE = "https://nexustasks.com/errors/";
    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        log.warn("Unauthorized access attempt to {} from IP {}",
                request.getRequestURI(),
                request.getRemoteAddr());

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiError error = ApiError.builder()
                .type(PROBLEM_TYPE_BASE + "unauthorized")
                .title("Authentication required")
                .status(ErrorCode.INVALID_CREDENTIALS.getHttpStatus())
                .detail("Authentication required. Please provide a valid token.")
                .errorCode(ErrorCode.INVALID_CREDENTIALS.name())
                .timestamp(Instant.now())
                .instance(request.getRequestURI())
                .build();

        objectMapper.writeValue(response.getOutputStream(), error);
    }
}