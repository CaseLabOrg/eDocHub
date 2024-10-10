INSERT INTO document_types(name) VALUES ('a');

INSERT INTO users(name) VALUES ('test');

INSERT INTO attributes(document_type_id, name, required) VALUES(1, 'd', false);
INSERT INTO attributes(document_type_id, name, required) VALUES(1, 'c', true);


INSERT INTO documents(user_id, type_id) VALUES (1, 1);

INSERT INTO values(attribute_id, document_id, value) VALUES (2,1, '52');