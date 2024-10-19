INSERT INTO document_types(name) VALUES ('testType');

INSERT INTO users(name, surname, email, password) VALUES ('admin', 'admin', 'admin', '$2a$12$7bm52CEAOmLGDdHzlo9ZFulaFzejGYHqxOfFeSNYxv.jEAMTK5WXa');

INSERT INTO attributes(name, required) VALUES('testAttr1', false);
INSERT INTO attributes(name, required) VALUES('testAttr2', true);


INSERT INTO documents(user_id, type_id) VALUES (1, 1);

INSERT INTO values(attribute_id, document_version_id, value) VALUES (2,1, '52');

INSERT INTO signatures(hash, placeholder_name, user_id) VALUES ('lkj41k4k1','section1', 1);

INSERT INTO roles(name) VALUES ('ADMIN');
INSERT INTO roles(name) VALUES ('USER');

INSERT INTO user_roles VALUES (1, 1);

INSERT INTO Document_Types_Attributes VALUES (1, 1);
INSERT INTO Document_Types_Attributes VALUES (2, 1);

INSERT INTO document_version(version_id,  document_id, title, description) VALUES (1, 1, 'hw.txt', 'test');

INSERT INTO signatures(hash, placeholder_name, user_id, document_version_id) VALUES ('testHash', 'testPlaceholder', 1, 1);