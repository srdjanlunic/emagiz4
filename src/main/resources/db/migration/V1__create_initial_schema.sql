-- V1: Create Initial Schema

-- Table for Organizations
CREATE TABLE "organization" (
    "id" UUID PRIMARY KEY,
    "name" TEXT NOT NULL,
    "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table for Departments
CREATE TABLE "department" (
    "id" UUID PRIMARY KEY,
    "name" TEXT NOT NULL,
    "description" TEXT,
    "organization_id" UUID,
    FOREIGN KEY ("organization_id") REFERENCES "organization"("id") ON DELETE SET NULL
);

-- Table for Roles
CREATE TABLE "role" (
    "id" SERIAL PRIMARY KEY,
    "name" TEXT NOT NULL UNIQUE,
    "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table for User Accounts
CREATE TABLE "useraccount" (
    "id" UUID PRIMARY KEY,
    "username" TEXT NOT NULL UNIQUE,
    "email" TEXT NOT NULL UNIQUE,
    "password_hash" TEXT NOT NULL,
    "first_name" TEXT,
    "last_name" TEXT,
    "role_id" INTEGER,
    "organization_id" UUID,
    "is_active" BOOLEAN DEFAULT true,
    "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP,
    FOREIGN KEY ("role_id") REFERENCES "role"("id"),
    FOREIGN KEY ("organization_id") REFERENCES "organization"("id") ON DELETE SET NULL
);

-- Join table for Users and Departments
CREATE TABLE "userdepartment" (
    "user_id" UUID NOT NULL,
    "department_id" UUID NOT NULL,
    "assigned_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY ("user_id", "department_id"),
    FOREIGN KEY ("user_id") REFERENCES "useraccount"("id") ON DELETE CASCADE,
    FOREIGN KEY ("department_id") REFERENCES "department"("id") ON DELETE CASCADE
);

-- Table for IT Systems
CREATE TABLE "itsystem" (
    "id" UUID PRIMARY KEY,
    "name" TEXT NOT NULL,
    "vendor" TEXT,
    "description" TEXT,
    "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table for System Implementations
CREATE TABLE "systemimplementation" (
    "id" UUID PRIMARY KEY,
    "system_id" UUID NOT NULL,
    "department_id" UUID,
    "data_classification" TEXT,
    "sensitive_customer_data" BOOLEAN,
    "risk_score" TEXT,
    "criticality_level" TEXT,
    "internet_facing" BOOLEAN,
    "version" TEXT,
    "environment" TEXT,
    "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP,
    FOREIGN KEY ("system_id") REFERENCES "itsystem"("id") ON DELETE CASCADE,
    FOREIGN KEY ("department_id") REFERENCES "department"("id") ON DELETE SET NULL
);

-- Join table for System Owners
CREATE TABLE "systemowner" (
    "user_id" UUID NOT NULL,
    "system_implementation_id" UUID NOT NULL,
    "assigned_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY ("user_id", "system_implementation_id"),
    FOREIGN KEY ("user_id") REFERENCES "useraccount"("id") ON DELETE CASCADE,
    FOREIGN KEY ("system_implementation_id") REFERENCES "systemimplementation"("id") ON DELETE CASCADE
);

-- Table for Vulnerabilities
CREATE TABLE "vulnerability" (
    "id" UUID PRIMARY KEY,
    "cve_id" TEXT,
    "description" TEXT,
    "severity" TEXT,
    "cvss_score" DECIMAL,
    "affected_products" TEXT,
    "vendor" TEXT,
    "published_date" DATE,
    "last_modified" TIMESTAMP,
    "imported_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table for Vulnerability Updates (History)
CREATE TABLE "vulnerabilityupdate" (
    "id" UUID PRIMARY KEY,
    "vulnerability_id" UUID NOT NULL,
    "updated_on" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "details" TEXT,
    "update_type" TEXT,
    "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY ("vulnerability_id") REFERENCES "vulnerability"("id") ON DELETE CASCADE
);

-- Join table for System Vulnerabilities
CREATE TABLE "systemvulnerability" (
    "id" UUID PRIMARY KEY,
    "system_id" UUID NOT NULL,
    "vulnerability_id" UUID NOT NULL,
    "status" VARCHAR(50),
    "assessment_date" TIMESTAMP,
    "notes" TEXT,
    FOREIGN KEY ("system_id") REFERENCES "itsystem"("id") ON DELETE CASCADE,
    FOREIGN KEY ("vulnerability_id") REFERENCES "vulnerability"("id") ON DELETE CASCADE
);

-- Table for Escalations
CREATE TABLE "escalation" (
    "id" UUID PRIMARY KEY,
    "system_vulnerability_id" UUID NOT NULL,
    "security_officer_id" UUID,
    "tech_expert_id" UUID,
    "escalation_reason" TEXT,
    "escalation_date" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "escalation_status" VARCHAR(50),
    "response" TEXT,
    "response_date" TIMESTAMP,
    FOREIGN KEY ("system_vulnerability_id") REFERENCES "systemvulnerability"("id") ON DELETE CASCADE,
    FOREIGN KEY ("security_officer_id") REFERENCES "useraccount"("id") ON DELETE SET NULL,
    FOREIGN KEY ("tech_expert_id") REFERENCES "useraccount"("id") ON DELETE SET NULL
);

-- Table for Vulnerability Matches
CREATE TABLE "vulnerabilitymatch" (
    "id" UUID PRIMARY KEY,
    "vulnerability_id" UUID NOT NULL,
    "system_implementation_id" UUID NOT NULL,
    "matched_by_ai" BOOLEAN,
    "ai_relevance_score" FLOAT,
    "matching_criteria" TEXT,
    "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY ("vulnerability_id") REFERENCES "vulnerability"("id") ON DELETE CASCADE,
    FOREIGN KEY ("system_implementation_id") REFERENCES "systemimplementation"("id") ON DELETE CASCADE
);

-- Table for Notifications
CREATE TABLE "notification" (
    "id" UUID PRIMARY KEY,
    "user_id" UUID,
    "match_id" UUID,
    "system_id" UUID,
    "vulnerability_id" UUID,
    "message" TEXT,
    "created_at" TIMESTAMP,
    "is_read" BOOLEAN,
    "type" TEXT,
    "priority" TEXT,
    "read_at" TIMESTAMP,
    FOREIGN KEY ("user_id") REFERENCES "useraccount"("id") ON DELETE SET NULL,
    FOREIGN KEY ("match_id") REFERENCES "vulnerabilitymatch"("id") ON DELETE SET NULL,
    FOREIGN KEY ("system_id") REFERENCES "itsystem"("id") ON DELETE SET NULL,
    FOREIGN KEY ("vulnerability_id") REFERENCES "vulnerability"("id") ON DELETE SET NULL
);

-- Table for Vulnerability Assessments
CREATE TABLE "vulnerabilityassessment" (
    "id" UUID PRIMARY KEY,
    "match_id" UUID,
    "assessed_by" UUID,
    "assessment_date" DATE,
    "status" TEXT,
    "explanation" TEXT,
    "escalated" BOOLEAN,
    "escalated_to" UUID,
    "due_date" TIMESTAMP,
    "completed_date" TIMESTAMP,
    "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP,
    FOREIGN KEY ("match_id") REFERENCES "vulnerabilitymatch"("id") ON DELETE SET NULL,
    FOREIGN KEY ("assessed_by") REFERENCES "useraccount"("id") ON DELETE SET NULL,
    FOREIGN KEY ("escalated_to") REFERENCES "useraccount"("id") ON DELETE SET NULL
);

-- Table for Assessment History
CREATE TABLE "assessmenthistory" (
    "id" UUID PRIMARY KEY,
    "assessment_id" UUID,
    "changed_by" UUID,
    "old_status" TEXT,
    "new_status" TEXT,
    "changed_reason" TEXT,
    "changed_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY ("assessment_id") REFERENCES "vulnerabilityassessment"("id") ON DELETE SET NULL,
    FOREIGN KEY ("changed_by") REFERENCES "useraccount"("id") ON DELETE SET NULL
);

-- Table for Report Logs
CREATE TABLE "reportlog" (
    "id" UUID PRIMARY KEY,
    "generated_by" UUID,
    "type" TEXT,
    "date_range_start" TIMESTAMP,
    "date_range_end" TIMESTAMP,
    "filters" TEXT,
    "file_path" TEXT,
    "file_format" TEXT,
    "generated_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "title" TEXT,
    FOREIGN KEY ("generated_by") REFERENCES "useraccount"("id") ON DELETE SET NULL
);

-- Table for Settings
CREATE TABLE "settings" (
    "key" TEXT PRIMARY KEY,
    "value" TEXT
); 