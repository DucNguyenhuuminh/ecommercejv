CREATE TABLE product_sku (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    sku_code VARCHAR(100) NOT NULL UNIQUE,
    stock INT NOT NULL DEFAULT 0,
    deleted_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_sku_product FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE UNIQUE INDEX idx_sku_product ON product_sku(product_id);

CREATE TABLE product_attribute (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    CONSTRAINT fk_attr_product FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE product_attribute_value (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_attribute_id BIGINT NOT NULL,
    value VARCHAR(100) NOT NULL,
    display_order INT DEFAULT 0,
    CONSTRAINT fk_attrval_attr FOREIGN KEY (product_attribute_id) REFERENCES product_attribute(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE product_sku_has_attribute_value (
    sku_id BIGINT AUTO_INCREMENT NOT NULL,
    attribute_value_id BIGINT NOT NULL,
    PRIMARY KEY (sku_id, attribute_value_id),
    CONSTRAINT fk_sav_sku FOREIGN KEY (sku_id) REFERENCES product_sku(id) ON DELETE CASCADE,
    CONSTRAINT fk_sav_value FOREIGN KEY (attribute_value_id) REFERENCES product_attribute_value(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

