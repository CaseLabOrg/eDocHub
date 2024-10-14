INSERT INTO document_types(id, name) VALUES (1, 'a');

INSERT INTO users(id, name, surname, email, password) VALUES (1, 'admin', 'admin', 'admin', '$2a$12$7bm52CEAOmLGDdHzlo9ZFulaFzejGYHqxOfFeSNYxv.jEAMTK5WXa');

INSERT INTO attributes(document_type_id, name, required) VALUES(1, 'test1', false);
INSERT INTO attributes(document_type_id, name, required) VALUES(1, 'test2', true);


INSERT INTO documents(user_id, type_id) VALUES (1, 1);

INSERT INTO values(attribute_id, document_id, value) VALUES (2,1, '52');

INSERT INTO signatures(hash, placeholder_name, user_id) VALUES ('lkj41k4k1','section1', 1);

INSERT INTO roles(id, name) VALUES (1, 'ADMIN');

INSERT INTO user_roles VALUES (1, 1);