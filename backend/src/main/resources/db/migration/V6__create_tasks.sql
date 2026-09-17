CREATE TABLE tasks (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       public_id VARCHAR(36) NOT NULL UNIQUE,
                       version BIGINT NOT NULL DEFAULT 0,
                       created_at TIMESTAMP(6) NOT NULL,
                       updated_at TIMESTAMP(6) NOT NULL,
                       created_by VARCHAR(100),
                       is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
                       deleted_at TIMESTAMP(6),

                       title VARCHAR(255) NOT NULL,
                       slug VARCHAR(255) NOT NULL,
                       description TEXT,
                       status VARCHAR(20) NOT NULL DEFAULT 'TODO',
                       priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
                       due_date TIMESTAMP(6),
                       completed_at TIMESTAMP(6),
                       archived BOOLEAN NOT NULL DEFAULT FALSE,
                       archived_at TIMESTAMP(6),
                       owner_id BIGINT NOT NULL,
                       category_id BIGINT,

                       CONSTRAINT fk_task_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE,
                       CONSTRAINT fk_task_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
                       CONSTRAINT uk_task_owner_slug UNIQUE (owner_id, slug),

                       INDEX idx_task_owner_id (owner_id),
                       INDEX idx_task_category_id (category_id),
                       INDEX idx_task_status (status),
                       INDEX idx_task_priority (priority),
                       INDEX idx_task_due_date (due_date),
                       INDEX idx_task_archived (archived),
                       INDEX idx_task_public_id (public_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;