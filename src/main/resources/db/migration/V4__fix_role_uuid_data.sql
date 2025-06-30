-- V4: Fix Role UUID Data Type Mismatch

-- First, we need to fix the data type mismatch between schema and actual database
-- The role table should use UUID IDs, and useraccount.role_id should reference UUIDs

-- Step 1: Add a temporary column to store UUID role IDs
ALTER TABLE "useraccount" ADD COLUMN role_id_temp UUID;

-- Step 2: Create mapping from integer role IDs to UUIDs 
-- Map: 1->ADMIN, 2->SYSTEM_OWNER, 3->SECURITY_OFFICER, 4->TECHNICAL_EXPERT
UPDATE "useraccount" 
SET role_id_temp = CASE role_id
    WHEN 1 THEN 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15'::UUID  -- ADMIN
    WHEN 2 THEN 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12'::UUID  -- SYSTEM_OWNER
    WHEN 3 THEN 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13'::UUID  -- SECURITY_OFFICER
    WHEN 4 THEN 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14'::UUID  -- TECHNICAL_EXPERT
    ELSE NULL
END;

-- Step 3: Drop the old integer role_id column and rename temp column
ALTER TABLE "useraccount" DROP COLUMN role_id;
ALTER TABLE "useraccount" RENAME COLUMN role_id_temp TO role_id;

-- Step 4: Fix the role table - drop and recreate with proper UUIDs
DROP TABLE IF EXISTS "role" CASCADE;

CREATE TABLE "role" (
    id UUID PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Step 5: Insert roles with proper UUIDs and descriptions
INSERT INTO "role" (id, name, description) VALUES 
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'SYSTEM_OWNER', 'Manages system implementations and assesses vulnerabilities.'),
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'SECURITY_OFFICER', 'Oversees security operations and vulnerability management.'),
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'TECHNICAL_EXPERT', 'Provides technical expertise on vulnerabilities and systems.'),
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15', 'ADMIN', 'System administrator with full access.');

-- Step 6: Re-add the foreign key constraint
ALTER TABLE "useraccount" ADD CONSTRAINT fk_useraccount_role FOREIGN KEY (role_id) REFERENCES "role"(id); 