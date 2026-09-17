CREATE TABLE categories (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            public_id VARCHAR(36) NOT NULL UNIQUE,
                            version BIGINT NOT NULL DEFAULT 0,
                            created_at TIMESTAMP(6) NOT NULL,
                            updated_at TIMESTAMP(6) NOT NULL,
                            created_by VARCHAR(100),
                            is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
                            deleted_at TIMESTAMP(6),

                            name VARCHAR(100) NOT NULL,
                            slug VARCHAR(100) NOT NULL,
                            description VARCHAR(500),
                            color VARCHAR(7) NOT NULL DEFAULT '#6b7280', -- Gris par défaut
                            owner_id BIGINT NOT NULL,

                            CONSTRAINT fk_category_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE,
                            CONSTRAINT uk_category_owner_slug UNIQUE (owner_id, slug),

                            INDEX idx_category_owner_id (owner_id),
                            INDEX idx_category_public_id (public_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;