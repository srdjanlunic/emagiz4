-- V2: Add Dummy Data

-- Insert Roles
INSERT INTO "role" ("id", "name") VALUES
(1, 'admin'),
(2, 'system_owner'),
(3, 'security_officer'),
(4, 'technical_expert')
ON CONFLICT (id) DO NOTHING;

-- Insert Organization
INSERT INTO "organization" ("id", "name") VALUES
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Global Tech Inc.');

-- Insert Departments
INSERT INTO "department" ("id", "name", "organization_id") VALUES
('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'Human Resources', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
('c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'Engineering', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11');

-- Insert Users
-- Passwords: admin123, system123, security123, tech123
INSERT INTO "useraccount" ("id", "username", "email", "password_hash", "first_name", "last_name", "role_id", "organization_id") VALUES
('d0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'demo_admin20', 'admin@globaltech.com', '$2a$10$s55amiS/O8F6VcY.cJ/l/uvoBzs9.7DwAVJYMSNHr7HquCZsf59rK', 'Demo', 'Admin', 1, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
('e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15', 'role_system', 'system@globaltech.com', '$2a$10$kMMWGjBqlXU6MG2EMNQxAObDKkZX1URqeZXu5.ZQw1k0RMj0u6NfK', 'System', 'Role', 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
('d1eebc99-9c0b-4ef8-bb6d-6bb9bd380a16', 'role_security', 'security@globaltech.com', '$2a$10$5aBO7aWL5QO99DXLRwy8FOOTETfYGtznvZw8RGcrqJNV0l/pvXT6m', 'Security', 'Role', 3, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
('d2eebc99-9c0b-4ef8-bb6d-6bb9bd380a17', 'role_tech', 'tech@globaltech.com', '$2a$10$7NXYVChZShnHrz72PDjZ3OXf/J2Vqi5teuPU7uYcCUEIvixErKAE.', 'Tech', 'Role', 4, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11');

-- Insert IT Systems
INSERT INTO "itsystem" ("id", "name", "vendor") VALUES
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16', 'SuperServer 1000', 'Server Co'),
('10eebc99-9c0b-4ef8-bb6d-6bb9bd380a17', 'DataCruncher 5', 'Data Corp');

-- Insert System Implementations
INSERT INTO "systemimplementation" ("id", "system_id", "department_id", "version") VALUES
('20eebc99-9c0b-4ef8-bb6d-6bb9bd380a18', 'f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16', 'c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', '1.0.0');

-- Assign System Owner
INSERT INTO "systemowner" ("user_id", "system_implementation_id") VALUES
('e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15', '20eebc99-9c0b-4ef8-bb6d-6bb9bd380a18');

-- Insert Vulnerability
INSERT INTO "vulnerability" ("id", "cve_id", "description", "severity", "vendor") VALUES
('30eebc99-9c0b-4ef8-bb6d-6bb9bd380a19', 'CVE-2024-0001', 'A critical remote code execution vulnerability.', 'CRITICAL', 'Server Co');

-- Link Vulnerability to System
INSERT INTO "systemvulnerability" ("id", "system_id", "vulnerability_id", "status") VALUES
('40eebc99-9c0b-4ef8-bb6d-6bb9bd380a20', 'f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16', '30eebc99-9c0b-4ef8-bb6d-6bb9bd380a19', 'OPEN');

-- Insert Notification
INSERT INTO "notification" ("id", "user_id", "message", "type", "priority", "created_at", "is_read") VALUES
('50eebc99-9c0b-4ef8-bb6d-6bb9bd380a21', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15', 'New vulnerability detected on SuperServer 1000', 'VULNERABILITY', 'HIGH', CURRENT_TIMESTAMP, false); 