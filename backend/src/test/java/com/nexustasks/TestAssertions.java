package com.nexustasks;

import com.nexustasks.common.exception.BusinessException;
import com.nexustasks.common.exception.ErrorCode;
import org.assertj.core.api.AbstractThrowableAssert;

/**
 * Helpers d'assertion custom pour les tests.
 * Simplifie la vérification des BusinessException.
 */
public final class TestAssertions {

    private TestAssertions() {}

    /**
     * Vérifie que l'exception est une BusinessException avec l'ErrorCode attendu.
     *
     * Usage :
     *   assertThatThrownBy(() -> service.doSomething())
     *       .isInstanceOf(BusinessException.class)
     *       .satisfies(hasErrorCode(ErrorCode.EMAIL_ALREADY_EXISTS));
     */
    public static java.util.function.Consumer<Throwable> hasErrorCode(ErrorCode expectedCode) {
        return ex -> {
            BusinessException bex = (BusinessException) ex;
            if (bex.getErrorCode() != expectedCode) {
                throw new AssertionError(
                        "Expected ErrorCode " + expectedCode + " but got " + bex.getErrorCode());
            }
        };
    }
}