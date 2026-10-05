CREATE TABLE order_has_discount (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    discount_id BIGINT NOT NULL,
    amount_applied DECIMAL(15,2) NOT NULL,
    CONSTRAINT fk_ohd_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_ohd_discount FOREIGN KEY (discount_id) REFERENCES discount(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_ohd_order ON order_has_discount(order_id);