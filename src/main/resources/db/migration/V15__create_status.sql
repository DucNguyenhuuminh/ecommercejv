CREATE TABLE status (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at DATETIME DEFAULT  CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT  CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO status (name, description) VALUES
('PENDING','Order just created, not yet processed'),
('PAID','Paid'),
('PROCESSING', 'Preparing the order'),
('SHIPPED','Handed over to the carrier'),
('DELIVERED','Delivered successfully'),
('CANCELLED','Cancelled');