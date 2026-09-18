package com.nexustasks.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Exception métier de l'application.
 *
 * À utiliser dans les services pour signaler une erreur métier qui doit
 * être traduite en réponse HTTP appropriée par le GlobalExceptionHandler.
 *
 * Exemple :
 *   throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
 *   throw new BusinessException(ErrorCode.INVALID_OTP, "Provided code was: 123");
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String detail;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
        this.detail = null;
    }

    public BusinessException(ErrorCode errorCode, String detail) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
        this.detail = detail;
    }

    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getDefaultMessage(), cause);
        this.errorCode = errorCode;
        this.detail = null;
    }
}