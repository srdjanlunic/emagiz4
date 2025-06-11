CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TYPE assessment_status AS ENUM (
  'functionality_not_used',
  'newer_version',
  'attack_vector_not_available',
  'accepted_risk',
  'update_planned',
  'change_planned',
  'unknown'
);

CREATE TABLE Organization (
                              id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                              name TEXT NOT NULL,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Department (
                            id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                            name TEXT NOT NULL,
                            description TEXT,
                            organization_id UUID REFERENCES Organization(id) ON DELETE CASCADE,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Role (
                      id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                      name TEXT NOT NULL,
                      description TEXT,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE UserAccount (
                             id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                             username TEXT UNIQUE NOT NULL,
                             password TEXT NOT NULL,
                             email TEXT UNIQUE NOT NULL,
                             first_name TEXT NOT NULL,
                             last_name TEXT NOT NULL,
                             role_id UUID REFERENCES Role(id),
                             organization_id UUID REFERENCES Organization(id),
                             is_active BOOLEAN DEFAULT TRUE,
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE UserDepartment (
                                user_id UUID REFERENCES UserAccount(id) ON DELETE CASCADE,
                                department_id UUID REFERENCES Department(id) ON DELETE CASCADE,
                                assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                PRIMARY KEY (user_id, department_id)
);

CREATE TABLE ITSystem (
                          id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                          name TEXT NOT NULL,
                          vendor TEXT,
                          description TEXT,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE SystemImplementation (
                                      id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                      system_id UUID REFERENCES ITSystem(id) ON DELETE CASCADE,
                                      department_id UUID REFERENCES Department(id) ON DELETE CASCADE,
                                      data_classification TEXT,
                                      criticality_level TEXT CHECK (criticality_level IN ('HIGH','MEDIUM','LOW')),
                                      internet_facing BOOLEAN DEFAULT FALSE,
                                      sensitive_customer_data BOOLEAN DEFAULT FALSE,
                                      risk_score INTEGER DEFAULT 0,
                                      version TEXT,
                                      environment TEXT,
                                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE SystemOwner (
                             user_id UUID REFERENCES UserAccount(id) ON DELETE CASCADE,
                             system_implementation_id UUID REFERENCES SystemImplementation(id) ON DELETE CASCADE,
                             assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             PRIMARY KEY (user_id, system_implementation_id)
);

CREATE TABLE Vulnerability (
                               id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                               cve_id TEXT UNIQUE NOT NULL,
                               description TEXT,
                               severity TEXT CHECK (severity IN ('CRITICAL','HIGH','MEDIUM','LOW')),
                               cvss_score DECIMAL(3,1),
                               affected_products TEXT,
                               vendor TEXT,
                               published_date TIMESTAMP,
                               last_modified TIMESTAMP,
                               imported_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               status VARCHAR(50) DEFAULT 'open'
);

CREATE TABLE VulnerabilityUpdate (
                                     id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                     vulnerability_id UUID REFERENCES Vulnerability(id) ON DELETE CASCADE,
                                     updated_on TIMESTAMP NOT NULL,
                                     update_type TEXT,
                                     details TEXT,
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE VulnerabilityMatch (
                                    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                    vulnerability_id UUID REFERENCES Vulnerability(id) ON DELETE CASCADE,
                                    system_implementation_id UUID REFERENCES SystemImplementation(id) ON DELETE CASCADE,
                                    matched_by_ai BOOLEAN DEFAULT FALSE,
                                    ai_relevance_score FLOAT,
                                    matching_criteria TEXT,
                                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    UNIQUE (vulnerability_id, system_implementation_id)
);

CREATE TABLE VulnerabilityAssessment (
                                         id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                         match_id UUID REFERENCES VulnerabilityMatch(id) ON DELETE CASCADE,
                                         assessed_by UUID REFERENCES UserAccount(id),
                                         assessed_by_role UUID REFERENCES Role(id),
                                         assessment_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                         status assessment_status NOT NULL,
                                         explanation TEXT,
                                         escalated BOOLEAN DEFAULT FALSE,
                                         escalated_to UUID REFERENCES UserAccount(id),
                                         due_date TIMESTAMP,
                                         completed_date TIMESTAMP,
                                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE AssessmentHistory (
                                   id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                   assessment_id UUID REFERENCES VulnerabilityAssessment(id) ON DELETE CASCADE,
                                   changed_by UUID REFERENCES UserAccount(id),
                                   old_status assessment_status,
                                   new_status assessment_status,
                                   change_reason TEXT,
                                   changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Notification (
                              id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                              user_id UUID REFERENCES UserAccount(id) ON DELETE CASCADE,
                              match_id UUID REFERENCES VulnerabilityMatch(id) ON DELETE CASCADE,
                              system_id UUID REFERENCES SystemImplementation(id),
                              vulnerability_id UUID REFERENCES Vulnerability(id),
                              message TEXT NOT NULL,
                              type TEXT,
                              priority TEXT CHECK (priority IN ('HIGH','MEDIUM','LOW')),
                              is_read BOOLEAN DEFAULT FALSE,
                              read_at TIMESTAMP,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ReportLog (
                           id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                           generated_by UUID REFERENCES UserAccount(id),
                           type TEXT,
                           title TEXT,
                           date_range_start TIMESTAMP,
                           date_range_end TIMESTAMP,
                           filters TEXT,
                           file_path TEXT,
                           file_format TEXT,
                           generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Junction table for many-to-many relationship between System and Vulnerability
CREATE TABLE SystemVulnerability (
    system_id UUID NOT NULL,
    vulnerability_id UUID NOT NULL,
    PRIMARY KEY (system_id, vulnerability_id),
    FOREIGN KEY (system_id) REFERENCES ITSystem(id) ON DELETE CASCADE,
    FOREIGN KEY (vulnerability_id) REFERENCES Vulnerability(id) ON DELETE CASCADE
);

