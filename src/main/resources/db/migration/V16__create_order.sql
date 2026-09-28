CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    status_id BIGINT NOT NULL,
    total_amount DECIMAL(15,2) NOT NULL,
    address_id BIGINT NOT NULL,
    shipping_fee DECIMAL(15,2) DEFAULT 0,
    shipping_method VARCHAR(100),
    tracking_number VARCHAR(100),
    note VARCHAR(500),
    created_at DATETIME DEFAULT  CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT  CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES user(id),
    CONSTRAINT fk_orders_status FOREIGN KEY (status_id) REFERENCES status(id),
    CONSTRAINT fk_orders_address FOREIGN KEY (address_id) REFERENCES address(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE order_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    price_at_purchase DECIMAL(15,2) NOT NULL,
    created_at DATETIME DEFAULT  CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT  CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_order_item_product FOREIGN KEY (product_id) REFERENCES  product(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE order_status_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    status_id BIGINT NOT NULL,
    noted VARCHAR(500),
    changed_by BIGINT NOT NULL,
    created_at DATETIME DEFAULT  CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT  CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_osh_order FOREIGN KEY (order_id) REFERENCES  orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_osh_status FOREIGN KEY (status_id) REFERENCES status(id),
    CONSTRAINT fk_osh_user FOREIGN KEY (changed_by) REFERENCES user(id),
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_orders_user ON orders(user_id);
CREATE INDEX idx_order_item_order ON order_item(order_id);
CREATE INDEX idx_osh_order ON order_status_history(order_id);