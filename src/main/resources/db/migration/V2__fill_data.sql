-- Insert into plans
INSERT INTO plans (name, description, price, max_users) 
VALUES 
('Test2', 'Plan test for 2 users', 1000, 2),
('Test5', 'Plan test for 5 users', 2000, 5),
('Test10', 'Plan test for 10 users', 2920, 10);

-- Insert into subscriptions
INSERT INTO subscriptions (plan_id, tenant_id, status) 
VALUES 
(1, 1, 'INACTIVE'),
(2, 2, 'INACTIVE'),
(3, 1, 'INACTIVE'),
(4, 3, 'INACTIVE'),
(5, 3, 'INACTIVE'),
(6, 2, 'INACTIVE');

-- Insert into invoices
INSERT INTO invoices(subscription_id, description, amount, status, created_date) 
VALUES 
(1, 'Test', 1000, 'AWAITING_PAYMENT', '2024-11-25'),
(2, 'Test', 2000, 'AWAITING_PAYMENT', '2024-11-25'),
(3, 'Test', 1000, 'AWAITING_PAYMENT', '2024-11-26'),
(4, 'Test', 2920, 'AWAITING_PAYMENT', '2024-11-27'),
(5, 'Test', 2920, 'AWAITING_PAYMENT', '2024-11-27'),
(6, 'Test', 2000, 'AWAITING_PAYMENT', '2024-11-27');

-- Insert into Departments
INSERT INTO Departments (id, name, is_alive)
VALUES
(1, 'HR', TRUE),
(2, 'IT', TRUE),
(3, 'Finance', TRUE),
(4, 'Marketing', TRUE),
(5, 'Legal', FALSE);

-- Insert into Tenants
INSERT INTO Tenants (id, name, created_at, owner_id, is_alive)
VALUES
(1, 'Tenant Alpha', NOW(), 1, TRUE),
(2, 'Tenant Beta', NOW(), 2, TRUE),
(3, 'Tenant Gamma', NOW(), 3, FALSE),
(4, 'Tenant Overflow', NOW(), 4, TRUE),
(5, 'Tenant Silent', NOW(), 5, FALSE),
(6, 'Tenant Chaotic', NOW(), 6, TRUE);

-- Insert into Users (Some tenants have many users, others few)
INSERT INTO Users (id, department_id, name, surname, email, password, is_alive, tenant_id)
VALUES
(1, 1, 'admin', 'admin', 'admin@example.com', 'pass123', TRUE, 1),
(2, 1, 'Bob', 'Brown', 'bob.brown@example.com', 'pass234', TRUE, 1),
(3, 2, 'Charlie', 'Johnson', 'charlie.j@example.com', 'pass345', TRUE, 1),
(4, NULL, 'Dana', 'White', 'dana.w@example.com', 'pass456', FALSE, 2),
(5, 3, 'Eve', 'Green', 'eve.g@example.com', 'pass567', TRUE, 3),
(6, NULL, 'Frank', 'Miller', 'frank.m@example.com', 'pass678', FALSE, 4),
(7, 4, 'Gina', 'James', 'gina.j@example.com', 'pass789', TRUE, 4),
(8, 4, 'Hank', 'Gray', 'hank.g@example.com', 'pass890', TRUE, 4),
(9, 5, 'Ivy', 'Brown', 'ivy.b@example.com', 'pass901', TRUE, 6),
(10, NULL, 'Jack', 'Hill', 'jack.h@example.com', 'pass012', TRUE, 6);

-- Insert into Document_Types (Skewed distribution)
INSERT INTO Document_Types (id, name, is_alive, tenant_id)
VALUES
(1, 'Invoice', TRUE, 1),
(2, 'Contract', TRUE, 1),
(3, 'Report', FALSE, 2),
(4, 'Memo', TRUE, 3),
(5, 'Policy', TRUE, 4),
(6, 'Manual', TRUE, 4),
(7, 'Spreadsheet', TRUE, 5),
(8, 'Presentation', FALSE, 6);

-- Insert into Documents (Imbalance: Tenant 1 has many documents, others fewer)
INSERT INTO Documents (id, user_id, type_id, is_alive)
VALUES
(1, 1, 1, TRUE),
(2, 2, 1, TRUE),
(3, 2, 2, FALSE),
(4, 3, 2, TRUE),
(5, 3, 2, TRUE),
(6, 7, 6, TRUE),
(7, 8, 6, TRUE),
(8, 8, 7, FALSE),
(9, 8, 7, TRUE),
(10, 10, 8, FALSE);

-- Insert into Document Versions (Skewed with older, outdated versions)
INSERT INTO Document_Version (id, version_id, document_id, title, description, created_at)
VALUES
(1, 1, 1, 'Invoice v1', 'First invoice draft', NOW()),
(2, 1, 2, 'Invoice v1', 'Duplicate invoice', NOW()),
(3, 2, 2, 'Invoice v2', 'Corrected invoice', NOW()),
(4, 1, 3, 'Contract v1', 'Initial contract draft', '2023-05-20'),
(5, 1, 4, 'Contract v1', 'Updated contract', '2024-01-01'),
(6, 1, 5, 'Contract v1', 'Another contract', '2024-03-15'),
(7, 2, 6, 'Manual v2', 'Updated manual', '2023-12-10'),
(8, 1, 7, 'Invoice v1', 'Approved invoice draft', NOW()),
(9, 1, 8, 'Contract v1', 'Approved contract draft', NOW()),
(10, 2, 9, 'Manual v2', 'Revised manual', '2024-01-15'),
(11, 1, 10, 'Invoice v1', 'Approved invoice for Tenant Alpha', '2024-01-10'),
(12, 1, 9, 'Contract v2', 'Approved contract for Tenant Beta', '2024-03-15'),
(13, 1, 5, 'Report v1', 'Approved report for Tenant Gamma', '2024-06-25'),
(14, 1, 7, 'Memo v2', 'Approved memo for Tenant Overflow', '2024-07-20'),
(15, 1, 3, 'Policy v1', 'Approved policy for Tenant Silent', '2024-09-01'),
(16, 1, 8, 'Manual v1', 'Approved manual for Tenant Chaotic', '2024-11-10');

-- Insert into Attributes (Heavily skewed for tenant 1)
INSERT INTO Attributes (id, name, required, is_alive, tenant_id)
VALUES
(1, 'Amount', TRUE, TRUE, 1),
(2, 'Date', TRUE, TRUE, 1),
(3, 'Signature', FALSE, TRUE, 1),
(4, 'Reviewer', FALSE, TRUE, 2),
(5, 'Policy Number', TRUE, TRUE, 4);

-- Insert into Values (Chaotic document attributes)
INSERT INTO Values (id, attribute_id, document_version_id, value)
VALUES
(1, 1, 1, '1000'),
(2, 2, 1, '2024-12-01'),
(3, 3, 2, 'Alice Smith'),
(4, 4, 4, 'John Reviewer'),
(5, 5, 7, 'POL-2024-005');

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
INSERT INTO Signatures (id, hash, placeholder_title, user_id, document_version_id)
VALUES
(1, 123456789, 'Sig 1', 1, 1),
(2, 987654321, 'Sig 2', 2, 2),
(3, 567890123, 'Sig 3', 2, 3),
(4, 102938475, 'Sig 4', 8, 7),
(5, 192837465, 'Sig 5', 8, 7),
(6, 111223344, 'Sig 6', 2, 8),
(7, 222334455, 'Sig 7', 3, 9),
(8, 333445566, 'Sig 8', 8, 10);

-- Insert into Votings (Skewed voting status)
INSERT INTO Votings (id, document_version_id, status, approval_threshold, current_approval_rate, created_at, deadline)
VALUES
(1, 1, 'Pending', 0.80, 0.40, NOW(), '2024-12-31'),
(2, 2, 'Approved', 0.90, 0.95, '2024-01-01', '2024-06-30'),
(3, 3, 'Rejected', 0.75, 0.20, NOW(), '2024-11-30'),
(4, 8, 'Approved', 0.85, 0.90, NOW(), '2024-12-31'),
(5, 9, 'Approved', 0.90, 0.92, '2024-10-01', '2025-01-01'),
(6, 10, 'Approved', 0.80, 0.85, NOW(), '2024-12-15');

-- Insert into Signature Requests (Imbalance with ignored and pending)
INSERT INTO Signature_Requests (id, user_id_to, voting_id, document_version_id, status)
VALUES
(1, 2, 1, 1, 'Pending'),
(2, 3, 1, 1, 'Ignored'),
(3, 3, 2, 7, 'Ignored'),
(4, 7, 3, 4, 'Rejected'),
(5, 3, 4, 8, 'Ignored'),
(6, 8, 5, 9, 'Approved'),
(7, 7, 6, 10, 'Ignored'),
(8, 3, 5, 11, 'Approved'),
(9, 8, 4, 12, 'Approved'),
(10, 7, 6, 13, 'Approved'),
(11, 2, 5, 14, 'Approved'),
(12, 1, 4, 15, 'Approved'),
(13, 7, 6, 16, 'Approved');
