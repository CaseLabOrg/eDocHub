INSERT INTO document_types(name, is_alive) VALUES ('testType', true);

INSERT INTO users(name, surname, email, password, is_alive) VALUES ('admin', 'admin', 'admin', '$2a$12$7bm52CEAOmLGDdHzlo9ZFulaFzejGYHqxOfFeSNYxv.jEAMTK5WXa', true);

INSERT INTO attributes(name, required, is_alive) VALUES('testAttr1', false, true);
INSERT INTO attributes(name, required, is_alive) VALUES('testAttr2', true, true);


INSERT INTO documents(user_id, type_id, is_alive, state) VALUES (1, 1, true, 'CREATED');

INSERT INTO values(attribute_id, document_version_id, value) VALUES (2,1, '52');

INSERT INTO roles(name) VALUES ('ADMIN');
INSERT INTO roles(name) VALUES ('USER');

INSERT INTO user_roles VALUES (1, 1);

INSERT INTO Document_Types_Attributes VALUES (1, 1);
INSERT INTO Document_Types_Attributes VALUES (2, 1);

INSERT INTO document_version(version_id,  document_id, title, filename, description, created_at) VALUES (1, 1, 'hw', 'hw.txt', 'test', '2024-12-12T23:59:59.425Z');

INSERT INTO signatures(hash, placeholder_title, user_id, document_version_id) VALUES (1322131231, 'testPlaceholder', 1, 1);

INSERT INTO comments(document_id, author_id, content, created_at) VALUES (1, 1, 'Test comment', '2024-12-12T23:59:59.425Z');
