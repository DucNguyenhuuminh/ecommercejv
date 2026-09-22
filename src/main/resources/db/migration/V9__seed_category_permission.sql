INSERT INTO permission (method, url, description) VALUES
    ('POST', '/api/categories/**','create category'),
    ('PUT', '/api/categories/**', 'edit category'),
    ('DELETE', '/api/categories/**', 'delete category');

INSERT INTO role_has_permission (role_id, permission_id)
SELECT r.id, p.id FROM role r, permission p
WHERE r.name = 'MANAGER' AND p.url = 'api/categories/**';