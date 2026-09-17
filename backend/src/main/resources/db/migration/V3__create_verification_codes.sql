CREATE TABLE verification_codes (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    public_id VARCHAR(36) NOT NULL UNIQUE,
                                    version BIGINT NOT NULL DEFAULT 0,
                                    created_at TIMESTAMP(6) NOT NULL,
                                    updated_at TIMESTAMP(6) NOT NULL,
                                    created_by VARCHAR(100),
                                    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
                                    deleted_at TIMESTAMP(6),

                                    user_id BIGINT NOT NULL,
                                    code_hash VARCHAR(64) NOT NULL,
                                    expires_at TIMESTAMP(6) NOT NULL,
                                    attempts INT NOT NULL DEFAULT 0,
                                    used BOOLEAN NOT NULL DEFAULT FALSE,

                                    CONSTRAINT fk_vc_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                    INDEX idx_vc_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;