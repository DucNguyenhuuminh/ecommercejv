INSERT INTO permission (method, url, description) VALUES
('GET','/api/cart/**','View user cart '),
('POST','/api/cart/**','Add product into cart'),
('PUT','/api/cart/**','Edit stock in cart'),
('DELETE','/api/cart/**','Delete product out of the cart');

INSERT INTO role_has_permission(role_id, permission_id)
SELECT r.id, p.id FROM role r, permission p
WHERE r.name = 'CUSTOMER' AND p.url = '/api/cart/**';