INSERT INTO tenants (name, created_at) VALUES ('Tenant1', NOW());

INSERT INTO tenants (name, created_at) VALUES ('Tenant2', NOW());


INSERT INTO users (name, surname, email, password, is_alive, tenant_id)
VALUES ('super_admin', 'admin', 'admin@example.com', '$2a$12$7bm52CEAOmLGDdHzlo9ZFulaFzejGYHqxOfFeSNYxv.jEAMTK5WXa', true, 2);

UPDATE tenants
SET admin_user = 1
WHERE name = 'Tenant2';


INSERT INTO document_types(name, is_alive, tenant_id) VALUES ('testType', true, 1);
INSERT INTO document_types(name, is_alive, tenant_id) VALUES ('testTpe', true, 2);

INSERT INTO attributes(name, required, is_alive, tenant_id) VALUES('testAttr1', false, true, 1);
INSERT INTO attributes(name, required, is_alive, tenant_id) VALUES('testAttr2', true, true, 2);


INSERT INTO documents(user_id, type_id, is_alive) VALUES (1, 1, true);

INSERT INTO values(attribute_id, document_version_id, value) VALUES (2,1, '52');

INSERT INTO roles(name) VALUES ('SUPER_ADMIN');
INSERT INTO roles(name) VALUES ('ADMIN');
INSERT INTO roles(name) VALUES ('USER');

INSERT INTO user_roles VALUES (1, 1);

INSERT INTO Document_Types_Attributes VALUES (1, 1);
INSERT INTO Document_Types_Attributes VALUES (2, 1);

INSERT INTO document_version(version_id,  document_id, title, description, created_at) VALUES (1, 1, 'hw.txt', 'test', '2024-12-12T23:59:59.425Z');

INSERT INTO signatures(hash, placeholder_title, user_id, document_version_id) VALUES (1322131231, 'testPlaceholder', 1, 1);

INSERT INTO comments(document_id, author_id, content, created_at) VALUES (1, 1, 'Test comment', '2024-12-12T23:59:59.425Z');

