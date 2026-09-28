INSERT INTO permission (method, url, description) VALUES
('GET','/api/addresses/**','Get user address'),
('POST','/api/addresses/**','Add  address'),
('PUT','/api/addresses/**','Edit address'),
('DELETE','/api/addresses/**','Delete address');

INSERT INTO role_has_permission(role_id, permission_id)
SELECT r.id, p.id FROM role r, permission p
WHERE r.name = 'CUSTOMER' AND p.url = '/api/addresses/**';