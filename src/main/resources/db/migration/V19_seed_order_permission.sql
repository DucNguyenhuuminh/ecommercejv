INSERT INTO permission (method, url, description) VALUES
('GET','/api/orders/**','View user order'),
('POST','/api/orders/checkout','Add order'),
('PUT','/api/orders/*/cancel','Cancel user order'),
('PUT','/api/orders/*/status','Update status order');

INSERT INTO role_has_permission(role_id, permission_id)
SELECT r.id, p.id FROM role r, permission p
WHERE r.name = 'CUSTOMER' AND p.url IN ('/api/orders/**', '/api/orders/checkout','/api/orders/*/cancel');

INSERT INTO role_has_permission(role_id, permission_id)
SELECT r.id, p.id FROM role r, permission p
WHERE r.name = 'MANAGER' AND p.url IN ('/api/orders/**', '/api/orders/*/status');