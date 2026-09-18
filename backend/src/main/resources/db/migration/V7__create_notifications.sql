CREATE TABLE notifications (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               public_id VARCHAR(36) NOT NULL UNIQUE,
                               version BIGINT NOT NULL DEFAULT 0,
                               created_at TIMESTAMP(6) NOT NULL,
                               updated_at TIMESTAMP(6) NOT NULL,
                               created_by VARCHAR(100),
                               is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
                               deleted_at TIMESTAMP(6),

                               recipient_id BIGINT NOT NULL,
                               type VARCHAR(30) NOT NULL,
                               title VARCHAR(255) NOT NULL,
                               message TEXT,
                               is_read BOOLEAN NOT NULL DEFAULT FALSE,
                               read_at TIMESTAMP(6),
                               resource_public_id VARCHAR(36), -- Lien vers la ressource concernée (ex: publicId de la tâche)

                               CONSTRAINT fk_notification_recipient FOREIGN KEY (recipient_id) REFERENCES users(id) ON DELETE CASCADE,
                               INDEX idx_notification_recipient (recipient_id),
                               INDEX idx_notification_read (is_read),
                               INDEX idx_notification_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;