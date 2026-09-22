CREATE TABLE refresh_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token_hash VARCHAR(64) NOT NULL,
    user_agent VARCHAR(255),
    ip VARCHAR(45),
    expires_at DATETIME NOT NULL ,
    created_at DATETIME DEFAULT  CURRENT_TIMESTAMP,
    CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_refresh_token_user_id ON refresh_token(user_id);
CREATE UNIQUE INDEX idx_refresh_token_hash ON refresh_token(token_hash);