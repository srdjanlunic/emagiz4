-- V3: Fix Database Schema Issues

-- Add missing created_at column to department table
ALTER TABLE "department" ADD COLUMN "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Update existing records to have a created_at timestamp
UPDATE "department" SET "created_at" = CURRENT_TIMESTAMP WHERE "created_at" IS NULL; 