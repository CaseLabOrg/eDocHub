INSERT INTO tenants (name, created_at, owner_id, is_alive) VALUES ('Tenant1', NOW(), 1, true);
INSERT INTO tenants (name, created_at, owner_id, is_alive) VALUES ('Tenant2', NOW(), 2, true);

INSERT INTO plans (name, description, price, max_users) VALUES ('Test5', 'Plan test for 5 users', 100, 5);

INSERT INTO subscriptions (plan_id, tenant_id, status) VALUES (1, 1, 'INACTIVE');
INSERT INTO subscriptions (plan_id, tenant_id, status) VALUES (1, 2, 'INACTIVE');

INSERT INTO invoices(subscription_id, description, amount, status, created_date) VALUES (1, 'Test', 100, 'AWAITING_PAYMENT', '2024-10-27');

INSERT INTO payments(payment_id, invoice_id, payment_method, status, created_at, idempotence_key) VALUES ('2eb2ef08-000f-5000-b000-1e1b56d1529a', 1, 'YOO_MONEY', 'PENDING', '2024-10-27T23:59:59.425Z', 'e92aba65-9678-4389-a484-68bd3deddf57');

INSERT INTO users (name, surname, email, password, is_alive, tenant_id) VALUES ('super_admin', 'admin', 'admin@example.com', '$2a$12$7bm52CEAOmLGDdHzlo9ZFulaFzejGYHqxOfFeSNYxv.jEAMTK5WXa', true, 1);
INSERT INTO users (name, surname, email, password, is_alive, tenant_id) VALUES ('super_admin2', 'admin2', 'admin2@example.com', '$2a$12$7bm52CEAOmLGDdHzlo9ZFulaFzejGYHqxOfFeSNYxv.jEAMTK5WXa', true, 2);

INSERT INTO document_types(name, is_alive, tenant_id) VALUES ('testType', true, 1);
INSERT INTO document_types(name, is_alive, tenant_id) VALUES ('testTpe', true, 2);

INSERT INTO attributes(name, required, is_alive, tenant_id) VALUES('testAttr1', false, true, 1);
INSERT INTO attributes(name, required, is_alive, tenant_id) VALUES('testAttr2', true, true, 2);


INSERT INTO documents(user_id, type_id, is_alive) VALUES (1, 1, true);
INSERT INTO documents(user_id, type_id, is_alive) VALUES (1, 2, true);

INSERT INTO values(attribute_id, document_version_id, value) VALUES (2,1, '52');

INSERT INTO roles(name) VALUES ('SUPER_ADMIN');
INSERT INTO roles(name) VALUES ('ADMIN');
INSERT INTO roles(name) VALUES ('OWNER');
INSERT INTO roles(name) VALUES ('USER');

INSERT INTO user_roles VALUES (1, 1);
INSERT INTO user_roles VALUES (1, 2);
INSERT INTO user_roles VALUES (1, 3);

INSERT INTO Document_Types_Attributes VALUES (1, 1);
INSERT INTO Document_Types_Attributes VALUES (2, 1);

INSERT INTO document_version(version_id,  document_id, title, description, created_at) VALUES (1, 1, 'hw.txt', 'test', '2024-12-12T23:59:59.425Z');

INSERT INTO signatures(hash, placeholder_title, user_id, document_version_id) VALUES (1322131231, 'testPlaceholder', 1, 1);

INSERT INTO comments(document_id, author_id, content, created_at) VALUES (1, 1, 'Test comment', '2024-12-12T23:59:59.425Z');

