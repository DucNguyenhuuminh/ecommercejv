CREATE TABLE auth_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type ENUM('VERIFY_EMAIL', 'RESET_PASSWORD') NOT NULL,
    token_hash VARCHAR(64) NOT NULL,
    expires_at DATETIME NOT NULL,
    created_at DATETIME DEFAULT  CURRENT_TIMESTAMP,
    CONSTRAINT fk_auth_token_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_auth_token_user_type ON auth_token(user_id, type);
CREATE UNIQUE INDEX idx_auth_token_hash ON auth_token(token_hash);