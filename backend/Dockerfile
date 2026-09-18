# =====================================================
# STAGE 1 : Build avec Maven
# =====================================================
FROM maven:3.9-eclipse-temurin-21-alpine AS build

# Répertoire de travail
WORKDIR /app

# Copier uniquement le pom.xml pour exploiter le cache Docker
# Les dépendances ne seront retéléchargées que si pom.xml change
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copier le code source et construire l'application
COPY src ./src
RUN mvn clean package -DskipTests -B

# =====================================================
# STAGE 2 : Runtime minimal
# =====================================================
FROM eclipse-temurin:21-jre-alpine AS runtime

# Installer curl pour les health checks
RUN apk add --no-cache curl

# Créer un utilisateur non-root pour la sécurité
RUN addgroup -S spring && adduser -S spring -G spring

# Répertoire de travail
WORKDIR /app

# Copier uniquement le JAR depuis le stage de build
COPY --from=build /app/target/*.jar app.jar

# Créer le dossier de stockage pour les avatars
RUN mkdir -p /app/storage/avatars && \
    chown -R spring:spring /app

# Basculer sur l'utilisateur non-root
USER spring:spring

# Exposer le port de l'application
EXPOSE 8080

# Variables d'environnement par défaut
ENV SPRING_PROFILES_ACTIVE=dev \
    JAVA_OPTS="-Xms256m -Xmx512m" \
    STORAGE_PATH=/app/storage

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/api/actuator/health || exit 1

# Point d'entrée
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]