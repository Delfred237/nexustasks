-- Ajout d'un champ pour tracer la rotation et détecter le vol
ALTER TABLE refresh_tokens
    ADD COLUMN replaced_by VARCHAR(255) NULL,
    ADD INDEX idx_refresh_token_replaced (replaced_by);