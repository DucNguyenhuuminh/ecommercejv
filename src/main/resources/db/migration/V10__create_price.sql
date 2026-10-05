CREATE TABLE price (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    price DECIMAL(15,2) NOT NULL,
    description VARCHAR(255),
    created_at DATETIME DEFAULT  CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT  CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE product_has_price (
    id BIGINT AUTO_INCREMENT NOT NULL,
    product_sku_id BIGINT NOT NULL,
    price_id BIGINT NOT NULL,
    PRIMARY KEY (product_sku_id, price_id),
    CONSTRAINT fk_php_product FOREIGN KEY (product_sku_id) REFERENCES product(id) ON DELETE CASCADE,
    CONSTRAINT fk_php_price FOREIGN KEY (price_id) REFERENCES price(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_php_product ON product_has_price(product_sku_id);