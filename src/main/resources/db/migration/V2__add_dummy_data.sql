-- V2: Add Comprehensive Dummy Data

-- Insert Roles if they don't exist
INSERT INTO "role" ("id", "name") VALUES
(1, 'admin'),
(2, 'system_owner'),
(3, 'security_officer'),
(4, 'technical_expert')
ON CONFLICT (id) DO NOTHING;

-- Insert Organizations
INSERT INTO "organization" ("id", "name") VALUES
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Global Tech Inc.'),
('a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'Innovate Solutions'),
('a2eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'Secure Systems Ltd.');

-- Insert Departments
INSERT INTO "department" ("id", "name", "organization_id") VALUES
('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'Human Resources', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
('c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'Engineering', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
('d0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'Product Development', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a12'),
('e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15', 'IT Support', 'a2eebc99-9c0b-4ef8-bb6d-6bb9bd380a13');

-- Insert Users with plaintext passwords
INSERT INTO "useraccount" ("id", "username", "email", "password", "first_name", "last_name", "role_id", "organization_id") VALUES
('8f9b76c2-5e41-4c44-9e87-7a8d2da0a386', 'admin', 'admin@globaltech.com', 'admin123', 'John', 'Doe', 1, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
('f9a8a7a8-6e41-4c44-9e87-7a8d2da0a387', 'sysowner', 'owner@globaltech.com', 'owner123', 'Jane', 'Smith', 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
('a8b7b6b5-5e41-4c44-9e87-7a8d2da0a388', 'secofficer', 'officer@innovate.com', 'officer123', 'Peter', 'Jones', 3, 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a12'),
('b7c6c5c4-6e41-4c44-9e87-7a8d2da0a389', 'techexpert', 'expert@secure.com', 'expert123', 'Mary', 'Williams', 4, 'a2eebc99-9c0b-4ef8-bb6d-6bb9bd380a13');

-- Assign users to departments
INSERT INTO "userdepartment" ("user_id", "department_id") VALUES
('8f9b76c2-5e41-4c44-9e87-7a8d2da0a386', 'c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13'),
('f9a8a7a8-6e41-4c44-9e87-7a8d2da0a387', 'c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13'),
('a8b7b6b5-5e41-4c44-9e87-7a8d2da0a388', 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14'),
('b7c6c5c4-6e41-4c44-9e87-7a8d2da0a389', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15');

-- Insert IT Systems
INSERT INTO "itsystem" ("id", "name", "vendor") VALUES
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16', 'SuperServer 1000', 'Server Co'),
('10eebc99-9c0b-4ef8-bb6d-6bb9bd380a17', 'DataCruncher 5', 'Data Corp'),
('20eebc99-9c0b-4ef8-bb6d-6bb9bd380a18', 'LegacySystem', 'Old Tech');

-- Insert System Implementations
INSERT INTO "systemimplementation" ("id", "system_id", "department_id", "version", "environment", "criticality_level") VALUES
('a3eebc99-9c0b-4ef8-bb6d-6bb9bd380a19', 'f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16', 'c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', '1.0.0', 'Production', 'HIGH'),
('b3eebc99-9c0b-4ef8-bb6d-6bb9bd380a20', '10eebc99-9c0b-4ef8-bb6d-6bb9bd380a17', 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', '2.3.1', 'Staging', 'MEDIUM'),
('c3eebc99-9c0b-4ef8-bb6d-6bb9bd380a21', '20eebc99-9c0b-4ef8-bb6d-6bb9bd380a18', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15', '0.9.0-beta', 'Development', 'LOW');

-- Assign System Owners
INSERT INTO "systemowner" ("user_id", "system_implementation_id") VALUES
('f9a8a7a8-6e41-4c44-9e87-7a8d2da0a387', 'a3eebc99-9c0b-4ef8-bb6d-6bb9bd380a19');

-- Insert Vulnerabilities
INSERT INTO "vulnerability" ("id", "cve_id", "description", "severity", "vendor") VALUES
('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 'CVE-2024-0001', 'A critical remote code execution vulnerability.', 'CRITICAL', 'Server Co'),
('e3eebc99-9c0b-4ef8-bb6d-6bb9bd380a23', 'CVE-2024-0002', 'A medium-severity data leakage flaw.', 'MEDIUM', 'Data Corp'),
('f3eebc99-9c0b-4ef8-bb6d-6bb9bd380a24', 'CVE-2021-1234', 'Outdated component causing denial-of-service.', 'HIGH', 'Old Tech');

-- Link Vulnerabilities to Systems
INSERT INTO "systemvulnerability" ("id", "system_id", "vulnerability_id", "status") VALUES
('a4eebc99-9c0b-4ef8-bb6d-6bb9bd380a25', 'f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16', 'd3eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 'OPEN'),
('b4eebc99-9c0b-4ef8-bb6d-6bb9bd380a26', '10eebc99-9c0b-4ef8-bb6d-6bb9bd380a17', 'e3eebc99-9c0b-4ef8-bb6d-6bb9bd380a23', 'IN_PROGRESS'),
('c4eebc99-9c0b-4ef8-bb6d-6bb9bd380a27', '20eebc99-9c0b-4ef8-bb6d-6bb9bd380a18', 'f3eebc99-9c0b-4ef8-bb6d-6bb9bd380a24', 'RESOLVED');

-- Insert Notifications
INSERT INTO "notification" ("id", "user_id", "message", "type", "priority", "created_at", "is_read") VALUES
('a5eebc99-9c0b-4ef8-bb6d-6bb9bd380a28', 'f9a8a7a8-6e41-4c44-9e87-7a8d2da0a387', 'New critical vulnerability CVE-2024-0001 detected on SuperServer 1000', 'VULNERABILITY', 'HIGH', CURRENT_TIMESTAMP, false),
('b5eebc99-9c0b-4ef8-bb6d-6bb9bd380a29', 'a8b7b6b5-5e41-4c44-9e87-7a8d2da0a388', 'Vulnerability assessment for CVE-2024-0002 is due.', 'TASK', 'MEDIUM', CURRENT_TIMESTAMP, false); 