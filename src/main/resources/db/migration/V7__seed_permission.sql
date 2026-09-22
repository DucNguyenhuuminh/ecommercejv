INSERT INTO permission (method, url, description) VALUES
    ('POST', '/api/products/**', 'create product'),
    ('PUT', '/api/products/**', 'edit product'),
    ('DELETE', '/api/product/**', 'delete product');

INSERT INTO role_has_permission (role_id, permission_id)
SELECT r.id, p.id FROM role r, permission p
WHERE r.name = 'MANAGER' AND p.url = 'api/products/**';