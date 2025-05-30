CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE Organization(
id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
name TEXT NOT NULL,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Department(
id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
name TEXT NOT NULL,
organization_id UUID REFERENCES Organization(id) ON DELETE CASCADE,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Role(
id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
name TEXT NOT NULL,
description TEXT,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE UserAccount(
id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
name TEXT NOT NULL,
email TEXT UNIQUE NOT NULL,
role_id UUID REFERENCES Role(id),
organization_id UUID REFERENCES Organization(id),
is_active BOOLEAN DEFAULT TRUE,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE UserDepartment(
user_id UUID REFERENCES UserAccount(id) ON DELETE CASCADE,
department_id UUID REFERENCES Department(id) ON DELETE CASCADE,
assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (user_id, department_id)
);

CREATE TABLE System(
id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
name TEXT NOT NULL,
vendor TEXT,
description TEXT,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE SystemImplementation(
id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
system_id UUID REFERENCES System(id) ON DELETE CASCADE,
department_id UUID REFERENCES Department(id) ON DELETE CASCADE,
data_type TEXT,
sensitive_customer_data BOOLEAN DEFAULT FALSE,
risk_level TEXT CHECK (risk_level IN ('Critical','High','Medium','Low')),
version TEXT,
environment TEXT,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE SystemOwner(
user_id UUID REFERENCES UserAccount(id) ON DELETE CASCADE,
system_implementation_id UUID REFERENCES SystemImplementation(id) ON DELETE CASCADE,
TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
KEY (user_id, system_implementation_id)
);

CREATE TABLE Vulnerability(
id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
cve_id TEXT UNIQUE NOT NULL,
title TEXT,
description TEXT,
published_date DATE,
severity TEXT CHECK (severity IN ('Critical','High','Medium','Low')),
cvss_score DECIMAL(3,1),
external_url TEXT,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE VulnerabilityUpdate(
id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
vulnerability_id UUID REFERENCES Vulnerability(id) ON DELETE CASCADE,
updated_on DATE NOT NULL,
update_type TEXT,
details TEXT,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE VulnerabilityMatch(
id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
vulnerability_id UUID REFERENCES Vulnerability(id) ON DELETE CASCADE,
system_implementation_id UUID REFERENCES SystemImplementation(id) ON DELETE CASCADE,
matched_by_ai BOOLEAN DEFAULT FALSE,
ai_relevance_score FLOAT,
matching_criteria TEXT,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
UNIQUE(vulnerability_id, system_implementation_id)
);

CREATE TYPE assessment_status AS ENUM (
  'functionality_not_used',
  'newer_version',
  'attack_vector_not_available',
  'accepted_risk',
  'update_planned',
  'change_planned',
  'unknown'
);

CREATE TABLE VulnerabilityAssessment(
id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
match_id UUID REFERENCES VulnerabilityMatch(id) ON DELETE CASCADE,
assessed_by UUID REFERENCES UserAccount(id),
assessed_by_role UUID REFERENCES Role(id),
date DATE NOT NULL DEFAULT CURRENT_DATE,
status assessment_status NOT NULL,
explanation TEXT,
escalated BOOLEAN DEFAULT FALSE,
escalated_to UUID REFERENCES UserAccount(id),
due_date DATE,
completed_date DATE,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE AssessmentHistory(
id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
assessment_id UUID REFERENCES VulnerabilityAssessment(id) ON DELETE CASCADE,
changed_by UUID REFERENCES UserAccount(id),
old_status assessment_status,
new_status assessment_status,
change_reason TEXT,
changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Notification(
 UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
user_id UUID REFERENCES UserAccount(id) ON DELETE CASCADE,
match_id UUID REFERENCES VulnerabilityMatch(id) ON DELETE CASCADE,
type TEXT,
subject TEXT,
message TEXT,
priority TEXT CHECK (priority IN ('High','Medium','Low')),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
is_read BOOLEAN DEFAULT FALSE,
read_at TIMESTAMP
);

CREATE TABLE ReportLog(
id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
generated_by UUID REFERENCES UserAccount(id),
type TEXT,
title TEXT,
date_range_start DATE,
date_range_end DATE,
filters TEXT,
file_path TEXT,
file_format TEXT,
generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_vuln_match_system ON VulnerabilityMatch(system_implementation_id);
CREATE INDEX idx_vuln_match_vuln ON VulnerabilityMatch(vulnerability_id);
CREATE INDEX idx_vuln_match_ai ON VulnerabilityMatch(matched_by_ai);
CREATE INDEX idx_assessment_match ON VulnerabilityAssessment(match_id);
CREATE INDEX idx_assessment_status ON VulnerabilityAssessment(status);
CREATE INDEX idx_assessment_user ON VulnerabilityAssessment(assessed_by);
CREATE INDEX idx_assessment_date ON VulnerabilityAssessment(date);
CREATE INDEX idx_notifications_user ON Notification(user_id);
CREATE INDEX idx_notifications_unread ON Notification(user_id, is_read);
CREATE INDEX idx_system_impl_dept ON SystemImplementation(department_id);
CREATE INDEX idx_system_impl_risk ON SystemImplementation(risk_level);
CREATE INDEX idx_vulnerability_severity ON Vulnerability(severity);
CREATE INDEX idx_vulnerability_date ON Vulnerability(published_date);

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ LANGUAGE 'plpgsql';

CREATE TRIGGER set_assessment_updated_at
    BEFORE UPDATE ON VulnerabilityAssessment
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER set_system_impl_updated_at
    BEFORE UPDATE ON SystemImplementation
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE OR REPLACE FUNCTION mark_notification_read()
RETURNS TRIGGER AS $$
BEGIN
  IF NEW.read_at IS NOT NULL AND OLD.read_at IS NULL THEN
    NEW.is_read = TRUE;
END IF;
RETURN NEW;
END;
$$ LANGUAGE 'plpgsql';

CREATE TRIGGER notification_read_trigger
    BEFORE UPDATE ON Notification
    FOR EACH ROW
    EXECUTE FUNCTION mark_notification_read();
