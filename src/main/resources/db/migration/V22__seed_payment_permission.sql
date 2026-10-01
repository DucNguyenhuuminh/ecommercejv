INSERT INTO permission (method, url, description) VALUES
('POST','/api/payments/checkout/**','Create payment for customer order');

INSERT INTO role_has_permission (role_id, permission_id)
SELECT r.id, p.id FROM role r, permission p
WHERE r.name = 'CUSTOMER' AND p.url = '/api/payments/checkout/**';