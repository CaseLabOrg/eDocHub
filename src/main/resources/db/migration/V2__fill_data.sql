-- Insert into Departments
INSERT INTO Departments (name, is_alive)
VALUES
('HR', TRUE),
('IT', TRUE),
('Finance', TRUE),
('Marketing', TRUE),
('Legal', FALSE);

-- Insert into Tenants
INSERT INTO Tenants (name, created_at, owner_id, is_alive)
VALUES
('Tenant Alpha', NOW(), 1, TRUE),
('Tenant Beta', NOW(), 2, TRUE),
('Tenant Gamma', NOW(), 3, FALSE),
('Tenant Overflow', NOW(), 4, TRUE),
('Tenant Silent', NOW(), 5, FALSE),
( 'Tenant Chaotic', NOW(), 6, TRUE);

-- Insert into Users (Some tenants have many users, others few)
INSERT INTO Users (department_id, name, surname, email, password, is_alive, tenant_id)
VALUES
( 1, 'admin', 'admin', 'admin', '$2a$12$7bm52CEAOmLGDdHzlo9ZFulaFzejGYHqxOfFeSNYxv.jEAMTK5WXa', TRUE, 1),
(1, 'Bob', 'Brown', 'bob.brown@example.com', '$2a$12$7bm52CEAOmLGDdHzlo9ZFulaFzejGYHqxOfFeSNYxv.jEAMTK5WXa', TRUE, 1),
( 2, 'Charlie', 'Johnson', 'charlie.j@example.com', '$2a$12$7bm52CEAOmLGDdHzlo9ZFulaFzejGYHqxOfFeSNYxv.jEAMTK5WXa', TRUE, 1),
( NULL, 'Dana', 'White', 'dana.w@example.com', '$2a$12$7bm52CEAOmLGDdHzlo9ZFulaFzejGYHqxOfFeSNYxv.jEAMTK5WXa', FALSE, 2),
( 3, 'Eve', 'Green', 'eve.g@example.com', '$2a$12$7bm52CEAOmLGDdHzlo9ZFulaFzejGYHqxOfFeSNYxv.jEAMTK5WXa', TRUE, 3),
( NULL, 'Frank', 'Miller', 'frank.m@example.com', '$2a$12$7bm52CEAOmLGDdHzlo9ZFulaFzejGYHqxOfFeSNYxv.jEAMTK5WXa', FALSE, 4),
( 4, 'Gina', 'James', 'gina.j@example.com', '$2a$12$7bm52CEAOmLGDdHzlo9ZFulaFzejGYHqxOfFeSNYxv.jEAMTK5WXa', TRUE, 4),
( 4, 'Hank', 'Gray', 'hank.g@example.com', '$2a$12$7bm52CEAOmLGDdHzlo9ZFulaFzejGYHqxOfFeSNYxv.jEAMTK5WXa', TRUE, 4),
( 5, 'Ivy', 'Brown', 'ivy.b@example.com', '$2a$12$7bm52CEAOmLGDdHzlo9ZFulaFzejGYHqxOfFeSNYxv.jEAMTK5WXa', TRUE, 6),
( NULL, 'Jack', 'Hill', 'jack.h@example.com', '$2a$12$7bm52CEAOmLGDdHzlo9ZFulaFzejGYHqxOfFeSNYxv.jEAMTK5WXa', TRUE, 6);

-- Insert into Document_Types (Skewed distribution)
INSERT INTO Document_Types (name, is_alive, tenant_id)
VALUES
( 'Invoice', TRUE, 1),
('Contract', TRUE, 1),
( 'Report', FALSE, 2),
( 'Memo', TRUE, 3),
( 'Policy', TRUE, 4),
( 'Manual', TRUE, 4),
( 'Spreadsheet', TRUE, 5),
( 'Presentation', FALSE, 6);

-- Insert into Documents (Imbalance: Tenant 1 has many documents, others fewer)
INSERT INTO Documents (user_id, type_id, is_alive, state)
VALUES
( 1, 1, TRUE, 'CREATED'),
( 2, 1, TRUE, 'CREATED'),
( 2, 2, FALSE, 'DELETED'),
(3, 2, TRUE, 'CREATED'),
( 3, 2, TRUE, 'CREATED'),
( 7, 6, TRUE, 'CREATED'),
( 8, 6, TRUE, 'CREATED'),
( 8, 7, FALSE, 'DELETED'),
( 8, 7, TRUE, 'CREATED'),
( 10, 8, FALSE, 'DELETED');

-- Insert into Document Versions (Skewed with older, outdated versions)
INSERT INTO Document_Version (version_id, document_id, title, description, created_at)
VALUES
( 1, 1, 'Invoice v1', 'First invoice draft', NOW()),
( 1, 2, 'Invoice v1', 'Duplicate invoice', NOW()),
( 2, 2, 'Invoice v2', 'Corrected invoice', NOW()),
( 1, 3, 'Contract v1', 'Initial contract draft', '2023-05-20'),
( 1, 4, 'Contract v1', 'Updated contract', '2024-01-01'),
( 1, 5, 'Contract v1', 'Another contract', '2024-03-15'),
( 2, 6, 'Manual v2', 'Updated manual', '2023-12-10'),
( 1, 7, 'Invoice v1', 'Approved invoice draft', NOW()),
( 1, 8, 'Contract v1', 'Approved contract draft', NOW()),
( 2, 9, 'Manual v2', 'Revised manual', '2024-01-15'),
( 1, 10, 'Invoice v1', 'Approved invoice for Tenant Alpha', '2024-01-10'),
( 1, 9, 'Contract v2', 'Approved contract for Tenant Beta', '2024-03-15'),
( 1, 5, 'Report v1', 'Approved report for Tenant Gamma', '2024-06-25'),
( 1, 7, 'Memo v2', 'Approved memo for Tenant Overflow', '2024-07-20'),
( 1, 3, 'Policy v1', 'Approved policy for Tenant Silent', '2024-09-01'),
( 1, 8, 'Manual v1', 'Approved manual for Tenant Chaotic', '2024-11-10');

-- Insert into Attributes (Heavily skewed for tenant 1)
INSERT INTO Attributes (name, required, is_alive, tenant_id)
VALUES
('Amount', TRUE, TRUE, 1),
('Date', TRUE, TRUE, 1),
('Signature', FALSE, TRUE, 1),
('Reviewer', FALSE, TRUE, 2),
('Policy Number', TRUE, TRUE, 4);

-- Insert into Values (Chaotic document attributes)
INSERT INTO Values (attribute_id, document_version_id, value)
VALUES
(1, 1, '1000'),
(2, 1, '2024-12-01'),
(3, 2, 'Alice Smith'),
(4, 4, 'John Reviewer'),
(5, 7, 'POL-2024-005');

-- Insert into Roles (Expanded for imbalance)
INSERT INTO Roles (id, name)
VALUES
(1, 'SUPER_ADMIN'),
(2, 'ADMIN'),
(3, 'OWNER'),
(4, 'USER'),
(5, 'GUEST');

-- Insert into User Roles (Skewed role assignments)
INSERT INTO User_Roles (user_id, role_id)
VALUES
(1, 1), (1, 2), (1, 3),
(2, 4), (2, 5),
(3, 1), (3, 2),
(4, 5),
(7, 3), (7, 4), (7, 5),
(8, 2);

-- Insert into Signatures (More signatures for tenant 1 and 4)
INSERT INTO Signatures (hash, placeholder_title, user_id, document_version_id)
VALUES
( 123456789, 'Sig 1', 1, 1),
( 987654321, 'Sig 2', 2, 2),
( 567890123, 'Sig 3', 2, 3),
(102938475, 'Sig 4', 8, 7),
(192837465, 'Sig 5', 8, 7),
( 111223344, 'Sig 6', 2, 8),
( 222334455, 'Sig 7', 3, 9),
(333445566, 'Sig 8', 8, 10);

-- Insert into Votings (Skewed voting status)
INSERT INTO Votings (document_version_id, status, approval_threshold, current_approval_rate, created_at, deadline)
VALUES
(1, 'Pending', 0.80, 0.40, NOW(), '2024-12-31'),
( 2, 'Approved', 0.90, 0.95, '2024-01-01', '2024-06-30'),
(3, 'Rejected', 0.75, 0.20, NOW(), '2024-11-30'),
( 8, 'Approved', 0.85, 0.90, NOW(), '2024-12-31'),
( 9, 'Approved', 0.90, 0.92, '2024-10-01', '2025-01-01'),
(10, 'Approved', 0.80, 0.85, NOW(), '2024-12-15');

-- Insert into Signature Requests (Imbalance with ignored and pending)
INSERT INTO Signature_Requests (user_id_to, voting_id, document_version_id, status)
VALUES
(2, 1, 1, 'Pending'),
(3, 1, 1, 'Ignored'),
( 3, 2, 7, 'Ignored'),
( 7, 3, 4, 'Rejected'),
( 3, 4, 8, 'Ignored'),
( 8, 5, 9, 'Approved'),
(7, 6, 10, 'Ignored'),
( 3, 5, 11, 'Approved'),
(8, 4, 12, 'Approved'),
( 7, 6, 13, 'Approved'),
(2, 5, 14, 'Approved'),
( 1, 4, 15, 'Approved'),
(7, 6, 16, 'Approved');
