package com.nexustasks.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Format standardisé des erreurs API basé sur RFC 7807 (Problem Details).
 *
 * Structure :
 * - type : URI identifiant le type d'erreur (pour documentation)
 * - title : Titre court lisible par l'humain
 * - status : Code HTTP
 * - detail : Explication détaillée spécifique à l'occurrence
 * - instance : URI de l'instance spécifique (optionnel)
 * - errorCode : Code métier interne (extension custom)
 * - timestamp : Date de l'erreur (extension custom)
 * - errors : Détail des erreurs de validation (extension custom)
 *
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc7807">RFC 7807</a>
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

    private String type;
    private String title;
    private int status;
    private String detail;
    private String instance;

    // Extensions custom
    private String errorCode;
    private Instant timestamp;
    private List<FieldError> errors;
    private Map<String, Object> metadata;

    @Data
    @Builder
    public static class FieldError {
        private String field;
        private String message;
        private Object rejectedValue;
    }
}