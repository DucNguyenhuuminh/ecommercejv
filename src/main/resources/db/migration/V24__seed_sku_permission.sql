INSERT INTO permission (method, url, description) VALUES
('POST','/api/products/*/skus','Create sku for product'),
('PUT','/api/products/skus/**','Edit sku'),
('DELETE','/api/products/skus/**','Delete sku');

INSERT INTO role_has_permission (role_id, permission_id)
SELECT r.id, p.id FROM role r, permission p
WHERE r.name = 'MANAGER' AND p.url IN ('/api/products/*/skus','/api/products/skus/**')