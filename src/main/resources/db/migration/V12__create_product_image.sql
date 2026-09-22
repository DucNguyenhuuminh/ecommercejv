CREATE TABLE product_image (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    url VARCHAR(500) NOT NULL,
    is_thumbnail BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT  CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT  CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE product_has_image (
    product_id BIGINT NOT NULL,
    image_id BIGINT NOT NULL,
    PRIMARY KEY (product_id, image_id),
    CONSTRAINT fk_phi_product FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE,
    CONSTRAINT fk_phi_category FOREIGN KEY (image_id) REFERENCES product_image(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;