INSERT INTO document_types(name) VALUES ('testType');

INSERT INTO users(name, surname, email, password) VALUES ('admin', 'admin', 'admin', '$2a$12$7bm52CEAOmLGDdHzlo9ZFulaFzejGYHqxOfFeSNYxv.jEAMTK5WXa');

INSERT INTO attributes(name, required) VALUES('testAttr1', false);
INSERT INTO attributes(name, required) VALUES('testAttr2', true);


INSERT INTO documents(user_id, type_id) VALUES (1, 1);

INSERT INTO values(attribute_id, document_version_id, value) VALUES (2,1, '52');

INSERT INTO roles(name) VALUES ('ADMIN');
INSERT INTO roles(name) VALUES ('USER');

INSERT INTO user_roles VALUES (1, 1);

INSERT INTO Document_Types_Attributes VALUES (1, 1);
INSERT INTO Document_Types_Attributes VALUES (2, 1);

INSERT INTO document_version(version_id,  document_id, title, description, created_at) VALUES (1, 1, 'hw.txt', 'test', '2024-12-12T23:59:59.425Z');

INSERT INTO signatures(hash, placeholder_title, user_id, document_version_id) VALUES (1322131231, 'testPlaceholder', 1, 1);

INSERT INTO comments(document_id, user_id, comment, created_at) VALUES (1, 1, 'Test comment', '2024-12-12T23:59:59.425Z');
