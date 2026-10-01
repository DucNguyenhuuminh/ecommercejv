CREATE TABLE payment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    gateway ENUM('VNPAY', 'MOMO', 'STRIPE') NOT NULL ,
    transaction_id VARCHAR(255),
    amount DECIMAL(15,2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    paid_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE =InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE UNIQUE INDEX idx_payment_transaction ON payment(transaction_id);

CREATE TABLE order_has_payment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    payment_id BIGINT NOT NULL,
    amount_applied DECIMAL(15,2) NOT NULL,
    CONSTRAINT fk_ohp_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_ohp_payment FOREIGN KEY (payment_id) REFERENCES  payment(id)
) ENGINE =InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_ohp_order ON order_has_payment(order_id);
