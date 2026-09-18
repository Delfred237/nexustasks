package com.nexustasks.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Classe de base pour tous les tests d'intégration.
 *
 * Le profil "testcontainers" utilise le format JDBC spécial "jdbc:tc:mysql:..."
 * qui démarre automatiquement un conteneur MySQL éphémère pour chaque contexte de test.
 *
 * @Testcontainers est conservé pour activer les fonctionnalités avancées
 * si on décide d'utiliser des conteneurs explicites plus tard (ex: Mailpit, Redis).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("testcontainers")
public abstract class BaseIntegrationTest {
    // Pas besoin de MySQLContainer explicite ni @DynamicPropertySource
    // Spring Boot gère tout via le format jdbc:tc:...
}