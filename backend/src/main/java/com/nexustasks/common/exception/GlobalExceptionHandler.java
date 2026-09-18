package com.nexustasks.common.exception;

import com.nexustasks.common.dto.ApiError;
import com.nexustasks.storage.exception.FileStorageException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.List;

/**
 * Gestionnaire global des exceptions pour les controllers REST.
 *
 * Transforme toutes les exceptions en réponses Problem Details (RFC 7807) cohérentes.
 * Garantit un format unique pour React et Flutter.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String PROBLEM_TYPE_BASE = "https://nexustasks.com/errors/";

    // =====================================================
    // BUSINESS EXCEPTIONS (nos erreurs custom)
    // =====================================================

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusinessException(
            BusinessException ex, HttpServletRequest request) {

        log.warn("Business error on {}: {} - {}",
                request.getRequestURI(), ex.getErrorCode(), ex.getMessage());

        ApiError error = ApiError.builder()
                .type(PROBLEM_TYPE_BASE + ex.getErrorCode().name().toLowerCase())
                .title(ex.getErrorCode().getDefaultMessage())
                .status(ex.getErrorCode().getHttpStatus())
                .detail(ex.getDetail() != null ? ex.getDetail() : ex.getMessage())
                .errorCode(ex.getErrorCode().name())
                .timestamp(Instant.now())
                .instance(request.getRequestURI())
                .build();

        return ResponseEntity.status(ex.getErrorCode().getHttpStatus()).body(error);
    }

    // =====================================================
    // VALIDATION ERRORS (Bean Validation)
    // =====================================================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationErrors(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        List<ApiError.FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> ApiError.FieldError.builder()
                        .field(fe.getField())
                        .message(fe.getDefaultMessage())
                        .rejectedValue(fe.getRejectedValue())
                        .build())
                .toList();

        ApiError error = ApiError.builder()
                .type(PROBLEM_TYPE_BASE + "validation-error")
                .title("Validation failed")
                .status(HttpStatus.BAD_REQUEST.value())
                .detail("The request contains invalid fields")
                .errorCode(ErrorCode.VALIDATION_ERROR.name())
                .timestamp(Instant.now())
                .instance(request.getRequestURI())
                .errors(fieldErrors)
                .build();

        log.warn("Validation error on {}: {} field(s) invalid",
                request.getRequestURI(), fieldErrors.size());

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingParams(
            MissingServletRequestParameterException ex, HttpServletRequest request) {

        ApiError error = ApiError.builder()
                .type(PROBLEM_TYPE_BASE + "missing-parameter")
                .title("Missing required parameter")
                .status(HttpStatus.BAD_REQUEST.value())
                .detail("Required parameter '" + ex.getParameterName() + "' is missing")
                .errorCode(ErrorCode.VALIDATION_ERROR.name())
                .timestamp(Instant.now())
                .instance(request.getRequestURI())
                .build();

        log.warn("Missing parameter on {}: {}", request.getRequestURI(), ex.getParameterName());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

        String typeName = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";

        ApiError error = ApiError.builder()
                .type(PROBLEM_TYPE_BASE + "type-mismatch")
                .title("Invalid parameter type")
                .status(HttpStatus.BAD_REQUEST.value())
                .detail(String.format("Parameter '%s' must be of type %s", ex.getName(), typeName))
                .errorCode(ErrorCode.VALIDATION_ERROR.name())
                .timestamp(Instant.now())
                .instance(request.getRequestURI())
                .build();

        log.warn("Type mismatch on {}: {}", request.getRequestURI(), ex.getName());
        return ResponseEntity.badRequest().body(error);
    }

    // =====================================================
    // MALFORMED REQUESTS
    // =====================================================

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleMalformedJson(
            HttpMessageNotReadableException ex, HttpServletRequest request) {

        ApiError error = ApiError.builder()
                .type(PROBLEM_TYPE_BASE + "malformed-json")
                .title("Malformed JSON request")
                .status(HttpStatus.BAD_REQUEST.value())
                .detail("The request body contains invalid JSON")
                .errorCode(ErrorCode.VALIDATION_ERROR.name())
                .timestamp(Instant.now())
                .instance(request.getRequestURI())
                .build();

        log.warn("Malformed JSON on {}: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {

        ApiError error = ApiError.builder()
                .type(PROBLEM_TYPE_BASE + "method-not-supported")
                .title("HTTP method not supported")
                .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                .detail("Method " + ex.getMethod() + " is not supported for this endpoint")
                .timestamp(Instant.now())
                .instance(request.getRequestURI())
                .build();

        log.warn("Method not supported on {}: {}", request.getRequestURI(), ex.getMethod());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(error);
    }

    // =====================================================
    // SECURITY EXCEPTIONS
    // =====================================================

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(
            BadCredentialsException ex, HttpServletRequest request) {

        log.warn("Bad credentials attempt on {}", request.getRequestURI());

        return buildErrorResponse(
                ErrorCode.INVALID_CREDENTIALS,
                request,
                "The email or password you entered is incorrect"
        );
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiError> handleDisabled(
            DisabledException ex, HttpServletRequest request) {

        return buildErrorResponse(ErrorCode.ACCOUNT_DISABLED, request, null);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ApiError> handleLocked(
            LockedException ex, HttpServletRequest request) {

        return buildErrorResponse(ErrorCode.ACCOUNT_LOCKED, request, null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {

        log.warn("Access denied on {} - {}", request.getRequestURI(), ex.getMessage());
        return buildErrorResponse(ErrorCode.ACCESS_DENIED, request, null);
    }

    // =====================================================
    // FILE UPLOAD EXCEPTIONS
    // =====================================================

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiError> handleMaxUploadSize(
            MaxUploadSizeExceededException ex, HttpServletRequest request) {

        return buildErrorResponse(ErrorCode.FILE_TOO_LARGE, request, null);
    }

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ApiError> handleFileStorage(
            FileStorageException ex, HttpServletRequest request) {

        log.error("File storage error on {}: {}", request.getRequestURI(), ex.getMessage());
        return buildErrorResponse(ErrorCode.FILE_STORAGE_ERROR, request, ex.getMessage());
    }

    // =====================================================
    // DATA INTEGRITY EXCEPTIONS
    // =====================================================

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ApiError> handleOptimisticLock(
            OptimisticLockingFailureException ex, HttpServletRequest request) {

        log.warn("Optimistic lock failure on {}: {}", request.getRequestURI(), ex.getMessage());
        return buildErrorResponse(
                ErrorCode.RESOURCE_VERSION_CONFLICT,
                request,
                "The resource has been modified by another user. Please refresh and try again."
        );
    }

    // =====================================================
    // 404 - RESOURCE NOT FOUND
    // =====================================================

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiError> handleNoResourceFound(
            NoResourceFoundException ex, HttpServletRequest request) {

        ApiError error = ApiError.builder()
                .type(PROBLEM_TYPE_BASE + "resource-not-found")
                .title("Resource not found")
                .status(HttpStatus.NOT_FOUND.value())
                .detail("The requested resource does not exist")
                .errorCode(ErrorCode.RESOURCE_NOT_FOUND.name())
                .timestamp(Instant.now())
                .instance(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // =====================================================
    // FALLBACK - INTERNAL SERVER ERROR
    // =====================================================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(
            Exception ex, HttpServletRequest request) {

        // IMPORTANT : Loguer l'erreur COMPLÈTE avec stack trace
        log.error("Unhandled exception on {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        // Mais ne PAS exposer les détails internes au client
        ApiError error = ApiError.builder()
                .type(PROBLEM_TYPE_BASE + "internal-error")
                .title("Internal server error")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .detail("An unexpected error occurred. Please try again later.")
                .errorCode(ErrorCode.INTERNAL_ERROR.name())
                .timestamp(Instant.now())
                .instance(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    // =====================================================
    // HELPER
    // =====================================================

    private ResponseEntity<ApiError> buildErrorResponse(
            ErrorCode errorCode, HttpServletRequest request, String detail) {

        ApiError error = ApiError.builder()
                .type(PROBLEM_TYPE_BASE + errorCode.name().toLowerCase().replace('_', '-'))
                .title(errorCode.getDefaultMessage())
                .status(errorCode.getHttpStatus())
                .detail(detail != null ? detail : errorCode.getDefaultMessage())
                .errorCode(errorCode.name())
                .timestamp(Instant.now())
                .instance(request.getRequestURI())
                .build();

        return ResponseEntity.status(errorCode.getHttpStatus()).body(error);
    }
}