-- V1__init_schema.sql
-- Création de la table users

CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       public_id VARCHAR(36) NOT NULL UNIQUE,
                       version BIGINT NOT NULL DEFAULT 0,
                       created_at TIMESTAMP(6) NOT NULL,
                       updated_at TIMESTAMP(6) NOT NULL,
                       created_by VARCHAR(100),
                       is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
                       deleted_at TIMESTAMP(6),

                       first_name VARCHAR(50) NOT NULL,
                       last_name VARCHAR(50) NOT NULL,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       role VARCHAR(20) NOT NULL,
                       enabled BOOLEAN NOT NULL DEFAULT FALSE,
                       email_verified BOOLEAN NOT NULL DEFAULT FALSE,
                       avatar_url VARCHAR(500),

                       INDEX idx_users_email (email),
                       INDEX idx_users_public_id (public_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;