-- Final working test data script
-- This version handles all required fields properly

-- Insert Organizations (with different IDs to avoid conflicts)
INSERT INTO Organization (id, name) VALUES
                                        ('550e8400-e29b-41d4-a716-446655440001', 'TechCorp Solutions'),
                                        ('550e8400-e29b-41d4-a716-446655440002', 'SecureIT Ltd')
    ON CONFLICT (id) DO NOTHING;

-- Insert Roles (with different IDs to avoid conflicts)
INSERT INTO Role (id, name, description) VALUES
                                             ('550e8400-e29b-41d4-a716-446655440010', 'admin', 'System Administrator with full access'),
                                             ('550e8400-e29b-41d4-a716-446655440011', 'security_officer', 'Security Officer managing vulnerabilities'),
                                             ('550e8400-e29b-41d4-a716-446655440012', 'system_owner', 'System Owner responsible for specific systems'),
                                             ('550e8400-e29b-41d4-a716-446655440013', 'technical_expert', 'Technical Expert for vulnerability assessment')
    ON CONFLICT (id) DO NOTHING;

-- Insert Departments
INSERT INTO Department (id, name, description, organization_id) VALUES
                                                                    ('550e8400-e29b-41d4-a716-446655440020', 'IT Infrastructure', 'Manages core IT systems', '550e8400-e29b-41d4-a716-446655440001'),
                                                                    ('550e8400-e29b-41d4-a716-446655440021', 'Development', 'Software development team', '550e8400-e29b-41d4-a716-446655440001'),
                                                                    ('550e8400-e29b-41d4-a716-446655440022', 'Security', 'Information Security department', '550e8400-e29b-41d4-a716-446655440001')
    ON CONFLICT (id) DO NOTHING;

-- Insert Test Users (with unique usernames to avoid conflicts)
INSERT INTO UserAccount (id, username, password, email, first_name, last_name, role_id, organization_id) VALUES
                                                                                                             ('550e8400-e29b-41d4-a716-446655440030', 'demo_admin', 'YWRtaW4xMjM=', 'demo_admin@techcorp.com', 'Demo', 'Admin', '550e8400-e29b-41d4-a716-446655440010', '550e8400-e29b-41d4-a716-446655440001'),
                                                                                                             ('550e8400-e29b-41d4-a716-446655440031', 'demo_security', 'c2VjdXJpdHkxMjM=', 'demo_security@techcorp.com', 'Demo', 'Security', '550e8400-e29b-41d4-a716-446655440011', '550e8400-e29b-41d4-a716-446655440001'),
                                                                                                             ('550e8400-e29b-41d4-a716-446655440032', 'demo_owner', 'c3lzdGVtMTIz', 'demo_owner@techcorp.com', 'Demo', 'Owner', '550e8400-e29b-41d4-a716-446655440012', '550e8400-e29b-41d4-a716-446655440001'),
                                                                                                             ('550e8400-e29b-41d4-a716-446655440033', 'demo_expert', 'dGVjaDEyMw==', 'demo_expert@techcorp.com', 'Demo', 'Expert', '550e8400-e29b-41d4-a716-446655440013', '550e8400-e29b-41d4-a716-446655440001')
    ON CONFLICT (username) DO NOTHING;

-- Assign users to departments
INSERT INTO UserDepartment (user_id, department_id) VALUES
                                                        ('550e8400-e29b-41d4-a716-446655440030', '550e8400-e29b-41d4-a716-446655440020'),
                                                        ('550e8400-e29b-41d4-a716-446655440031', '550e8400-e29b-41d4-a716-446655440022'),
                                                        ('550e8400-e29b-41d4-a716-446655440032', '550e8400-e29b-41d4-a716-446655440021'),
                                                        ('550e8400-e29b-41d4-a716-446655440033', '550e8400-e29b-41d4-a716-446655440020')
    ON CONFLICT (user_id, department_id) DO NOTHING;

-- Insert IT Systems
INSERT INTO ITSystem (id, name, vendor, description) VALUES
                                                         ('550e8400-e29b-41d4-a716-446655440040', 'Apache HTTP Server', 'Apache Software Foundation', 'Web server software'),
                                                         ('550e8400-e29b-41d4-a716-446655440041', 'MySQL Database', 'Oracle Corporation', 'Relational database management system'),
                                                         ('550e8400-e29b-41d4-a716-446655440042', 'Windows Server', 'Microsoft Corporation', 'Server operating system'),
                                                         ('550e8400-e29b-41d4-a716-446655440043', 'Jenkins CI/CD', 'Jenkins Project', 'Continuous integration server')
    ON CONFLICT (id) DO NOTHING;

-- Insert System Implementations
INSERT INTO SystemImplementation (id, system_id, department_id, data_classification, criticality_level, internet_facing, sensitive_customer_data, risk_score, version, environment) VALUES
                                                                                                                                                                                        ('550e8400-e29b-41d4-a716-446655440050', '550e8400-e29b-41d4-a716-446655440040', '550e8400-e29b-41d4-a716-446655440020', 'CONFIDENTIAL', 'HIGH', true, true, 85, '2.4.54', 'Production'),
                                                                                                                                                                                        ('550e8400-e29b-41d4-a716-446655440051', '550e8400-e29b-41d4-a716-446655440041', '550e8400-e29b-41d4-a716-446655440021', 'SENSITIVE', 'HIGH', false, true, 75, '8.0.33', 'Production'),
                                                                                                                                                                                        ('550e8400-e29b-41d4-a716-446655440052', '550e8400-e29b-41d4-a716-446655440042', '550e8400-e29b-41d4-a716-446655440020', 'INTERNAL', 'MEDIUM', false, false, 45, '2019', 'Development'),
                                                                                                                                                                                        ('550e8400-e29b-41d4-a716-446655440053', '550e8400-e29b-41d4-a716-446655440043', '550e8400-e29b-41d4-a716-446655440021', 'INTERNAL', 'MEDIUM', true, false, 55, '2.401.3', 'Staging')
    ON CONFLICT (id) DO NOTHING;

-- Assign system owners
INSERT INTO SystemOwner (user_id, system_implementation_id) VALUES
                                                                ('550e8400-e29b-41d4-a716-446655440032', '550e8400-e29b-41d4-a716-446655440050'),
                                                                ('550e8400-e29b-41d4-a716-446655440032', '550e8400-e29b-41d4-a716-446655440051'),
                                                                ('550e8400-e29b-41d4-a716-446655440032', '550e8400-e29b-41d4-a716-446655440052'),
                                                                ('550e8400-e29b-41d4-a716-446655440032', '550e8400-e29b-41d4-a716-446655440053')
    ON CONFLICT (user_id, system_implementation_id) DO NOTHING;

-- Insert Sample Vulnerabilities
INSERT INTO Vulnerability (id, cve_id, description, severity, cvss_score, affected_products, vendor, published_date, last_modified) VALUES
                                                                                                                                        ('550e8400-e29b-41d4-a716-446655440060', 'CVE-2023-44487', 'HTTP/2 Rapid Reset Attack vulnerability in web servers', 'HIGH', 7.5, 'cpe:2.3:a:apache:http_server:*:*:*:*:*:*:*:*', 'Apache Software Foundation', '2023-10-10 10:00:00', '2023-10-15 14:30:00'),
                                                                                                                                        ('550e8400-e29b-41d4-a716-446655440061', 'CVE-2023-22884', 'MySQL Server vulnerability allowing privilege escalation', 'MEDIUM', 6.5, 'cpe:2.3:a:oracle:mysql:*:*:*:*:*:*:*:*', 'Oracle Corporation', '2023-01-17 09:00:00', '2023-01-20 11:15:00'),
                                                                                                                                        ('550e8400-e29b-41d4-a716-446655440062', 'CVE-2023-21709', 'Windows Server Remote Code Execution vulnerability', 'CRITICAL', 9.8, 'cpe:2.3:o:microsoft:windows_server_2019:*:*:*:*:*:*:*:*', 'Microsoft Corporation', '2023-02-14 16:00:00', '2023-02-16 10:45:00'),
                                                                                                                                        ('550e8400-e29b-41d4-a716-446655440063', 'CVE-2023-27905', 'Jenkins arbitrary file read vulnerability', 'HIGH', 7.5, 'cpe:2.3:a:jenkins:jenkins:*:*:*:*:*:*:*:*', 'Jenkins Project', '2023-03-08 12:00:00', '2023-03-10 08:30:00')
    ON CONFLICT (cve_id) DO NOTHING;

-- Insert Vulnerability Matches
INSERT INTO VulnerabilityMatch (id, vulnerability_id, system_implementation_id, matched_by_ai, matching_criteria) VALUES
                                                                                                                      ('550e8400-e29b-41d4-a716-446655440070', '550e8400-e29b-41d4-a716-446655440060', '550e8400-e29b-41d4-a716-446655440050', false, 'String-based matching on Apache HTTP Server'),
                                                                                                                      ('550e8400-e29b-41d4-a716-446655440071', '550e8400-e29b-41d4-a716-446655440061', '550e8400-e29b-41d4-a716-446655440051', false, 'String-based matching on MySQL Database'),
                                                                                                                      ('550e8400-e29b-41d4-a716-446655440072', '550e8400-e29b-41d4-a716-446655440062', '550e8400-e29b-41d4-a716-446655440052', false, 'String-based matching on Windows Server'),
                                                                                                                      ('550e8400-e29b-41d4-a716-446655440073', '550e8400-e29b-41d4-a716-446655440063', '550e8400-e29b-41d4-a716-446655440053', false, 'String-based matching on Jenkins CI/CD')
    ON CONFLICT (id) DO NOTHING;

-- Insert Vulnerability Assessments
INSERT INTO VulnerabilityAssessment (id, match_id, assessed_by, assessed_by_role, status, explanation) VALUES
                                                                                                           ('550e8400-e29b-41d4-a716-446655440080', '550e8400-e29b-41d4-a716-446655440070', '550e8400-e29b-41d4-a716-446655440032', '550e8400-e29b-41d4-a716-446655440012', 'update_planned', 'Critical vulnerability requires immediate patching'),
                                                                                                           ('550e8400-e29b-41d4-a716-446655440081', '550e8400-e29b-41d4-a716-446655440071', '550e8400-e29b-41d4-a716-446655440032', '550e8400-e29b-41d4-a716-446655440012', 'newer_version', 'Already running patched version 8.0.33'),
                                                                                                           ('550e8400-e29b-41d4-a716-446655440082', '550e8400-e29b-41d4-a716-446655440072', '550e8400-e29b-41d4-a716-446655440032', '550e8400-e29b-41d4-a716-446655440012', 'accepted_risk', 'Development environment with limited exposure'),
                                                                                                           ('550e8400-e29b-41d4-a716-446655440083', '550e8400-e29b-41d4-a716-446655440073', '550e8400-e29b-41d4-a716-446655440032', '550e8400-e29b-41d4-a716-446655440012', 'change_planned', 'Scheduling Jenkins upgrade for next maintenance window')
    ON CONFLICT (id) DO NOTHING;

-- Insert Sample Notifications with all required fields
INSERT INTO Notification (id, user_id, match_id, system_id, vulnerability_id, message, type, priority, is_read) VALUES
                                                                                                                    ('550e8400-e29b-41d4-a716-446655440090', '550e8400-e29b-41d4-a716-446655440032', '550e8400-e29b-41d4-a716-446655440070', '550e8400-e29b-41d4-a716-446655440050', '550e8400-e29b-41d4-a716-446655440060', 'New critical vulnerability CVE-2023-44487 detected for Apache HTTP Server in Production environment', 'VULNERABILITY_MATCH', 'HIGH', false),
                                                                                                                    ('550e8400-e29b-41d4-a716-446655440091', '550e8400-e29b-41d4-a716-446655440032', '550e8400-e29b-41d4-a716-446655440071', '550e8400-e29b-41d4-a716-446655440051', '550e8400-e29b-41d4-a716-446655440061', 'New vulnerability CVE-2023-22884 detected for MySQL Database in Production environment', 'VULNERABILITY_MATCH', 'MEDIUM', true),
                                                                                                                    ('550e8400-e29b-41d4-a716-446655440092', '550e8400-e29b-41d4-a716-446655440031', '550e8400-e29b-41d4-a716-446655440072', '550e8400-e29b-41d4-a716-446655440052', '550e8400-e29b-41d4-a716-446655440062', 'Critical vulnerability CVE-2023-21709 requires security review for Windows Server', 'VULNERABILITY_MATCH', 'HIGH', false)
    ON CONFLICT (id) DO NOTHING;

-- Show what we've added
SELECT 'Test data insertion completed successfully!' as status;
SELECT 'Total Users: ' || COUNT(*) as summary FROM UserAccount
UNION ALL
SELECT 'Total Systems: ' || COUNT(*) FROM ITSystem
UNION ALL
SELECT 'Total Vulnerabilities: ' || COUNT(*) FROM Vulnerability
UNION ALL
SELECT 'Total Notifications: ' || COUNT(*) FROM Notification
UNION ALL
SELECT 'Total Matches: ' || COUNT(*) FROM VulnerabilityMatch
UNION ALL
SELECT 'Total Assessments: ' || COUNT(*) FROM VulnerabilityAssessment;
