package com.nexustasks.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration centralisée d'OpenAPI / Swagger pour l'application NexusTasks.
 *
 * Utilise des schémas inline (pas de $ref) pour éviter tout problème de résolution.
 */
@Configuration
public class OpenApiConfig {

    public static final String BEARER_AUTH = "BearerAuth";
    public static final String COOKIE_AUTH = "CookieAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("NexusTasks API")
                        .version("1.0.0")
                        .description("""
                                API REST professionnelle de gestion de tâches.
                                
                                ## Authentification
                                - **Bearer Token (JWT)** : Pour clients mobile et outils de test.
                                - **HttpOnly Cookies** : Pour le client web (React), sécurisé contre XSS.
                                
                                ## Format des erreurs
                                Toutes les erreurs suivent le format **RFC 7807 (Problem Details)**.
                                """)
                        .contact(new Contact()
                                .name("NexusTasks Team")
                                .email("support@nexustasks.com")
                                .url("https://nexustasks.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH))
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH, bearerSecurityScheme())
                        .addSecuritySchemes(COOKIE_AUTH, cookieSecurityScheme())
                        // Enregistrer ApiError pour qu'il apparaisse dans la section "Schemas"
                        .addSchemas("ApiError", buildApiErrorSchema()));
    }

    private SecurityScheme bearerSecurityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Token JWT obtenu via `/api/auth/login`");
    }

    private SecurityScheme cookieSecurityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.COOKIE)
                .name("access_token")
                .description("Cookie HttpOnly défini automatiquement après login");
    }

    /**
     * Construit le schéma ApiError (RFC 7807) avec les types Swagger natifs.
     * Pas de $ref, pas de ModelConverters : 100% fiable.
     */
    private Schema<?> buildApiErrorSchema() {
        Schema<?> schema = new Schema<>()
                .type("object")
                .description("Erreur API au format RFC 7807 Problem Details");

        schema.addProperty("type", new StringSchema()
                .description("URI identifiant le type d'erreur (pour documentation)")
                .example("https://nexustasks.com/errors/validation-error"));

        schema.addProperty("title", new StringSchema()
                .description("Titre court lisible par l'humain")
                .example("Validation failed"));

        schema.addProperty("status", new IntegerSchema()
                .description("Code HTTP de l'erreur")
                .example(400));

        schema.addProperty("detail", new StringSchema()
                .description("Explication détaillée spécifique à l'occurrence")
                .example("The request contains invalid fields"));

        schema.addProperty("errorCode", new StringSchema()
                .description("Code métier interne pour réaction programmatique côté client")
                .example("VALIDATION_ERROR"));

        schema.addProperty("timestamp", new StringSchema()
                .format("date-time")
                .description("Date et heure ISO 8601 de l'erreur")
                .example("2026-09-18T10:30:45.123Z"));

        schema.addProperty("instance", new StringSchema()
                .description("URI de la requête ayant causé l'erreur")
                .example("/api/example"));

        // Sous-schéma FieldError pour les erreurs de validation
        Schema<?> fieldErrorSchema = new Schema<>()
                .type("object")
                .description("Détail d'une erreur de validation sur un champ spécifique");
        fieldErrorSchema.addProperty("field", new StringSchema()
                .description("Nom du champ en erreur")
                .example("email"));
        fieldErrorSchema.addProperty("message", new StringSchema()
                .description("Message d'erreur")
                .example("must be a well-formed email address"));
        fieldErrorSchema.addProperty("rejectedValue", new ObjectSchema()
                .description("Valeur rejetée")
                .example("invalid-email"));

        schema.addProperty("errors", new ArraySchema()
                .description("Liste des erreurs de validation (si applicable)")
                .items(fieldErrorSchema));

        schema.addProperty("metadata", new ObjectSchema()
                .description("Métadonnées additionnelles (optionnel)"));

        return schema;
    }

    /**
     * Customizer qui ajoute les réponses d'erreur standard à TOUS les endpoints.
     * Utilise le schéma inline (pas de $ref) pour éviter les erreurs de résolution.
     */
    @Bean
    public OpenApiCustomizer globalErrorResponsesCustomizer() {
        return openApi -> {
            ApiResponse unauthorized = buildErrorResponse(401, "Unauthorized",
                    "Authentication required or invalid token", "INVALID_CREDENTIALS");
            ApiResponse forbidden = buildErrorResponse(403, "Forbidden",
                    "Insufficient permissions or not resource owner", "ACCESS_DENIED");
            ApiResponse notFound = buildErrorResponse(404, "Not Found",
                    "The requested resource does not exist", "RESOURCE_NOT_FOUND");
            ApiResponse internalError = buildErrorResponse(500, "Internal Server Error",
                    "An unexpected error occurred", "INTERNAL_ERROR");

            openApi.getPaths().forEach((path, pathItem) ->
                    pathItem.readOperations().forEach(operation -> {
                        ApiResponses responses = operation.getResponses();
                        if (responses == null) {
                            responses = new ApiResponses();
                            operation.setResponses(responses);
                        }
                        responses.addApiResponse("401", unauthorized);
                        responses.addApiResponse("403", forbidden);
                        responses.addApiResponse("404", notFound);
                        responses.addApiResponse("500", internalError);
                    })
            );
        };
    }

    /**
     * Construit une réponse d'erreur avec le schéma ApiError INLINE.
     * Pas de $ref : le schéma est directement inclus dans la réponse.
     */
    private ApiResponse buildErrorResponse(int status, String title, String detail, String errorCode) {
        return new ApiResponse()
                .description(title)
                .content(new Content()
                        .addMediaType("application/json",
                                new MediaType()
                                        .schema(buildApiErrorSchema())  // Schéma inline, pas de $ref
                                        .example(buildExample(status, title, detail, errorCode))));
    }

    private Map<String, Object> buildExample(int status, String title, String detail, String errorCode) {
        Map<String, Object> example = new LinkedHashMap<>();
        example.put("type", "https://nexustasks.com/errors/" + errorCode.toLowerCase().replace('_', '-'));
        example.put("title", title);
        example.put("status", status);
        example.put("detail", detail);
        example.put("errorCode", errorCode);
        example.put("timestamp", "2026-09-18T10:30:45.123Z");
        example.put("instance", "/api/example");
        return example;
    }

    @Bean
    public OpenApiCustomizer tagsCustomizer() {
        return openApi -> openApi.setTags(List.of(
                new Tag().name("Authentication").description("Inscription, login, refresh, logout, vérification email"),
                new Tag().name("Users").description("Profil utilisateur et avatar"),
                new Tag().name("Tasks").description("Gestion des tâches, archivage, historique"),
                new Tag().name("Categories").description("Catégories de tâches"),
                new Tag().name("Notifications").description("Notifications in-app"),
                new Tag().name("Files").description("Accès aux fichiers uploadés (avatars)"),
                new Tag().name("Admin").description("Endpoints réservés aux administrateurs")
        ));
    }
}