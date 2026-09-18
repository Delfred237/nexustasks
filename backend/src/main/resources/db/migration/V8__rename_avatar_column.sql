-- Renomme avatar_url en avatar_path pour refléter le stockage sur disque
ALTER TABLE users CHANGE COLUMN avatar_url avatar_path VARCHAR(500) NULL;