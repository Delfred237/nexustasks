# NexusTasks — Backend API

API REST Spring Boot 3.5 (Java 21) consommée par les clients React et Flutter.

## Prérequis

- JDK 21
- Maven 3.9+ (ou wrapper `./mvnw`)
- Docker (pour MySQL + Mailpit en dev)

## Démarrage

```bash
# 1. Infrastructure locale (MySQL + Mailpit)
cd ../docker && docker compose up -d mysql mailpit

# 2. Application (profil dev : emails simulés/console ou Mailpit via profil mail)
cd ../backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev,mail
```

- API : http://localhost:8080/api
- Swagger UI : http://localhost:8080/swagger-ui.html
- OpenAPI JSON : http://localhost:8080/v3/api-docs

## Tests

```bash
./mvnw test          # unitaires + intégration (Testcontainers démarre MySQL tout seul)
```

## Configuration

| Variable | Rôle | Défaut dev |
|---|---|---|
| `JWT_SECRET` | Clé HMAC-SHA256 (base64, ≥ 256 bits) | clé dev |
| `CORS_ORIGINS` | Origines autorisées | localhost:5173, localhost:3000 |
| `SPRING_DATASOURCE_URL` | JDBC URL | localhost:3307 |
| `MAIL_HOST` / `MAIL_PORT` | SMTP | localhost:1025 (Mailpit) |
| `STORAGE_PATH` | Racine du stockage avatars | ./storage |

Profils : `dev` (Mailpit/console), `test`/`testcontainers` (tests), `prod` (SMTP réel, cookies Secure).

## Docker

```bash
docker build -t nexustasks-backend .
docker run -p 8080:8080 --env-file .env nexustasks-backend
```

Image multi-stage (build Maven → runtime JRE Alpine, utilisateur non-root).

## Conventions

- Architecture feature-based : `auth/`, `task/`, `category/`, `notification/`, `user/`, `audit/`
- Toutes les erreurs au format **RFC 7807 Problem Details** (`errorCode` métier inclus)
- Ownership checks : une ressource d'un autre utilisateur renvoie **404**, pas **403**
  