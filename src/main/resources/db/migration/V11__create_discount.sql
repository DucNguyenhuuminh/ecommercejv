CREATE TABLE discount (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    type ENUM('PERCENTAGE', 'FIXED_AMOUNT') NOT NULL,
    sale DECIMAL(15,2) NOT NULL,
    min_order_amount DECIMAL(15,2),
    max_discount_amount DECIMAL(15,2),
    usage_limit INT,
    used_count INT DEFAULT 0,
    start_date DATETIME,
    end_date DATETIME,
    description VARCHAR(500),
    deleted_at DATETIME,
    created_at DATETIME DEFAULT  CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT  CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE product_has_discount (
    id BIGINT AUTO_INCREMENT PRIMARY KEY ,
    product_id BIGINT NOT NULL,
    discount_id BIGINT NOT NULL,
    CONSTRAINT fk_phd_product FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE,
    CONSTRAINT fk_phd_discount FOREIGN KEY (discount_id) REFERENCES discount(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_phd_product ON product_has_discount(product_id);
