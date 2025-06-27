-- Drop existing escalations table if it exists (likely has wrong schema)
DROP TABLE IF EXISTS "escalations" CASCADE;

-- Create escalations table with correct UUID structure
CREATE TABLE "escalations" (
    "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    "system_vulnerability_id" UUID NOT NULL,
    "security_officer_id" UUID NOT NULL,
    "escalation_reason" TEXT NOT NULL,
    "escalation_date" TIMESTAMP NOT NULL,
    "escalation_status" VARCHAR(50) NOT NULL,
    "tech_expert_id" UUID NULL, -- NULL means available to all experts (pool)
    "response" TEXT NULL,
    "response_date" TIMESTAMP NULL,
    CONSTRAINT "fk_system_vulnerability" FOREIGN KEY("system_vulnerability_id") REFERENCES "system_vulnerabilities"("id") ON DELETE CASCADE,
    CONSTRAINT "fk_security_officer" FOREIGN KEY("security_officer_id") REFERENCES "UserAccount"("id") ON DELETE CASCADE,
    CONSTRAINT "fk_tech_expert" FOREIGN KEY("tech_expert_id") REFERENCES "UserAccount"("id") ON DELETE SET NULL
); 