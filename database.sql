-- ENUM type for assessment status
CREATE TYPE assessment_status AS ENUM (
  'functionality_not_used',
  'newer_version',
  'attack_vector_not_available',
  'accepted_risk',
  'update_planned',
  'change_planned',
  'unknown'
);

-- Organization Table
CREATE TABLE Organization (
    id UUID PRIMARY KEY,
    name TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Department Table
CREATE TABLE Department (
    id UUID PRIMARY KEY,
    name TEXT NOT NULL,
    organization_id UUID REFERENCES Organization(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Role Table
CREATE TABLE Role (
    id UUID PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- UserAccount Table
CREATE TABLE UserAccount (
    id UUID PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    role_id UUID REFERENCES Role(id),
    organization_id UUID REFERENCES Organization(id),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- UserDepartment Join Table
CREATE TABLE UserDepartment (
    user_id UUID REFERENCES UserAccount(id) ON DELETE CASCADE,
    department_id UUID REFERENCES Department(id) ON DELETE CASCADE,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, department_id)
);

-- System Table
CREATE TABLE System (
    id UUID PRIMARY KEY,
    name TEXT NOT NULL,
    vendor TEXT,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- SystemImplementation Table
CREATE TABLE SystemImplementation (
    id UUID PRIMARY KEY,
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

-- SystemOwner Join Table
CREATE TABLE SystemOwner (
    user_id UUID REFERENCES UserAccount(id) ON DELETE CASCADE,
    system_implementation_id UUID REFERENCES SystemImplementation(id) ON DELETE CASCADE,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, system_implementation_id)
);

-- Vulnerability Table
CREATE TABLE Vulnerability (
    id UUID PRIMARY KEY,
    cve_id TEXT UNIQUE NOT NULL,
    title TEXT,
    description TEXT,
    published_date DATE,
    severity TEXT CHECK (severity IN ('Critical','High','Medium','Low')),
    cvss_score DECIMAL(3,1),
    external_url TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- VulnerabilityUpdate Table
CREATE TABLE VulnerabilityUpdate (
    id UUID PRIMARY KEY,
    vulnerability_id UUID REFERENCES Vulnerability(id) ON DELETE CASCADE,
    updated_on DATE NOT NULL,
    update_type TEXT,
    details TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- VulnerabilityMatch Table
CREATE TABLE VulnerabilityMatch (
    id UUID PRIMARY KEY,
    vulnerability_id UUID REFERENCES Vulnerability(id) ON DELETE CASCADE,
    system_implementation_id UUID REFERENCES SystemImplementation(id) ON DELETE CASCADE,
    matched_by_ai BOOLEAN DEFAULT FALSE,
    ai_relevance_score FLOAT,
    matching_criteria TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (vulnerability_id, system_implementation_id)
);

-- VulnerabilityAssessment Table
CREATE TABLE VulnerabilityAssessment (
    id UUID PRIMARY KEY,
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

-- AssessmentHistory Table
CREATE TABLE AssessmentHistory (
    id UUID PRIMARY KEY,
    assessment_id UUID REFERENCES VulnerabilityAssessment(id) ON DELETE CASCADE,
    changed_by UUID REFERENCES UserAccount(id),
    old_status assessment_status,
    new_status assessment_status,
    change_reason TEXT,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Notification Table
CREATE TABLE Notification (
    id UUID PRIMARY KEY,
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

-- ReportLog Table
CREATE TABLE ReportLog (
    id UUID PRIMARY KEY,
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
