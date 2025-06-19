-- Truncate all tables to reset the database state before seeding
TRUNCATE TABLE 
    Organization, 
    Role, 
    Department, 
    UserAccount, 
    ITSystem, 
    SystemImplementation, 
    Vulnerability,
    VulnerabilityMatch,
    VulnerabilityAssessment
RESTART IDENTITY CASCADE;

-- Insert dummy data into the database

-- Organization
INSERT INTO Organization (id, name) VALUES ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'eMagiz');

-- Roles
INSERT INTO Role (id, name, description) VALUES 
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'SYSTEM_OWNER', 'Manages system implementations and assesses vulnerabilities.'),
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'SECURITY_OFFICER', 'Oversees security operations and vulnerability management.'),
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'TECHNICAL_EXPERT', 'Provides technical expertise on vulnerabilities and systems.'),
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15', 'ADMIN', 'System administrator with full access.');

-- Departments
INSERT INTO Department (id, name, description, organization_id) VALUES
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16', 'Human Resources', 'Manages employee-related tasks.', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a17', 'Engineering', 'Develops and maintains software products.', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a18', 'Sales', 'Manages customer relationships and sales.', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11');

-- Users
-- Passwords are Bcrypt hashed. 
-- demo_owner: owner123
-- demo_security: security123
-- demo_tech: tech123
-- demo_admin: admin123
INSERT INTO UserAccount (id, username, password, email, first_name, last_name, role_id, organization_id) VALUES
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a19', 'demo_owner', 'owner123', 'owner@emagiz.com', 'Demo', 'Owner', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a20', 'demo_security', 'security123', 'security@emagiz.com', 'Demo', 'Security', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a21', 'demo_tech', 'tech123', 'tech@emagiz.com', 'Demo', 'Tech', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 'demo_admin', 'admin123', 'admin@emagiz.com', 'Demo', 'Admin', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11');


-- IT Systems
INSERT INTO ITSystem (id, name, vendor, description) VALUES
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a23', 'eMagiz Platform', 'eMagiz', 'The main integration platform.'),
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a24', 'Customer Portal', 'In-house', 'Portal for customer interaction.'),
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a25', 'HR Management System', 'Workday', 'System for managing HR processes.');

-- System Implementations
INSERT INTO SystemImplementation (id, system_id, department_id, data_classification, criticality_level, internet_facing, sensitive_customer_data, version, environment) VALUES
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a26', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a23', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a17', 'CONFIDENTIAL', 'HIGH', true, true, '2023.4', 'Production'),
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a27', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a24', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a18', 'SENSITIVE', 'MEDIUM', true, true, '2.1', 'Production'),
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a28', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a25', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16', 'INTERNAL', 'MEDIUM', false, false, 'v3', 'Internal');

-- Vulnerabilities (from the service)
INSERT INTO Vulnerability (id, cve_id, description, severity, cvss_score, affected_products, vendor, published_date, last_modified) VALUES
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a29', 'CVE-2024-12345', 'Critical remote code execution vulnerability in Apache HTTP Server', 'CRITICAL', 9.8, 'cpe:2.3:a:apache:http_server:*:*:*:*:*:*:*:*', 'Apache Software Foundation', '2024-01-15 00:00:00', '2024-01-16 00:00:00'),
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a30', 'CVE-2024-54321', 'SQL injection vulnerability in MySQL Server', 'HIGH', 8.1, 'cpe:2.3:a:oracle:mysql:*:*:*:*:*:*:*:*', 'Oracle Corporation', '2024-02-10 00:00:00', '2024-02-11 00:00:00'),
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a31', 'CVE-2024-98765', 'Cross-site scripting vulnerability in WordPress', 'MEDIUM', 6.1, 'cpe:2.3:a:wordpress:wordpress:*:*:*:*:*:*:*:*', 'WordPress Foundation', '2024-03-05 00:00:00', '2024-03-06 00:00:00'); 