-- Add vendor column to vulnerabilities table
ALTER TABLE "vulnerabilities" ADD COLUMN "vendor" VARCHAR(255); 