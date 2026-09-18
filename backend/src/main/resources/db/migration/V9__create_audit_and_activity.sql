-- Table d'audit global (sécurité/admin)
CREATE TABLE audit_logs (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            public_id VARCHAR(36) NOT NULL UNIQUE,
                            version BIGINT NOT NULL DEFAULT 0,
                            created_at TIMESTAMP(6) NOT NULL,
                            updated_at TIMESTAMP(6) NOT NULL,
                            created_by VARCHAR(100),
                            is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
                            deleted_at TIMESTAMP(6),

                            action VARCHAR(50) NOT NULL,
                            actor_public_id VARCHAR(36), -- UUID de l'utilisateur qui a fait l'action
                            actor_email VARCHAR(100),
                            resource_type VARCHAR(50), -- ex: "TASK", "USER", "CATEGORY"
                            resource_public_id VARCHAR(36),
                            ip_address VARCHAR(45), -- IPv6 peut faire jusqu'à 45 caractères
                            details TEXT, -- Informations supplémentaires (JSON par exemple)

                            INDEX idx_audit_action (action),
                            INDEX idx_audit_actor (actor_public_id),
                            INDEX idx_audit_created_at (created_at),
                            INDEX idx_audit_resource (resource_type, resource_public_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table d'historique d'activité des tâches (métier/UX)
CREATE TABLE task_activities (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 public_id VARCHAR(36) NOT NULL UNIQUE,
                                 version BIGINT NOT NULL DEFAULT 0,
                                 created_at TIMESTAMP(6) NOT NULL,
                                 updated_at TIMESTAMP(6) NOT NULL,
                                 created_by VARCHAR(100),
                                 is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
                                 deleted_at TIMESTAMP(6),

                                 task_id BIGINT NOT NULL,
                                 actor_id BIGINT NOT NULL,
                                 activity_type VARCHAR(30) NOT NULL,
                                 description TEXT,

                                 CONSTRAINT fk_activity_task FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
                                 CONSTRAINT fk_activity_actor FOREIGN KEY (actor_id) REFERENCES users(id) ON DELETE CASCADE,
                                 INDEX idx_activity_task_id (task_id),
                                 INDEX idx_activity_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;