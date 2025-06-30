# Database Compatibility Fixes

## Overview
This document outlines the database schema fixes implemented to resolve slow loading issues and system errors in the vulnerability management system.

## Issues Resolved

### 1. Missing `created_at` Column in Department Table
**Problem**: The `DepartmentDAO.mapResultSetToDepartment()` method was trying to access a `created_at` column that didn't exist in the database schema.

**Error**: 
```
org.postgresql.util.PSQLException: The column name created_at was not found in this ResultSet.
```

**Solution**: Created migration `V3__fix_database_schema.sql` to add the missing column:
```sql
ALTER TABLE "department" ADD COLUMN "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
UPDATE "department" SET "created_at" = CURRENT_TIMESTAMP WHERE "created_at" IS NULL;
```

### 2. Table Name Mismatch in EscalationDAO
**Problem**: The database schema created a table named `escalation` (singular), but the `EscalationDAO` was referencing `escalations` (plural) in all SQL queries.

**Error**:
```
org.postgresql.util.PSQLException: ERROR: relation "escalations" does not exist
```

**Solution**: Updated all SQL queries in `EscalationDAO.java` to use the correct table name `escalation` instead of `escalations`.

### 3. Escalation ID Type Mismatch
**Problem**: The database schema used `UUID` for the escalation table's primary key, but the Java model and DAO used `Integer`, causing escalation creation to fail.

**Error**: 
```
Failed to escalate CVE (frontend error due to backend failure)
```

**Solution**: Updated the following files to use `UUID` consistently:
- `model/Escalation.java` - Changed ID field from `Integer` to `UUID`
- `dao/EscalationDAO.java` - Updated create method to generate UUID manually instead of expecting auto-generated integers
- `dto/EscalationDto.java` - Changed ID field type to `UUID`
- `service/EscalationService.java` - Updated all method signatures to use `UUID`
- `resource/EscalationResource.java` - Updated path parameter parsing to use `UUID.fromString()`

## Files Modified

### Database Migration
- `src/main/resources/db/migration/V3__fix_database_schema.sql` (created)

### Code Changes
- `src/main/java/dao/EscalationDAO.java` - Fixed table name references and ID type consistency
- `src/main/java/model/Escalation.java` - Changed ID field from Integer to UUID
- `src/main/java/dto/EscalationDto.java` - Updated ID field type to UUID
- `src/main/java/service/EscalationService.java` - Updated method signatures for UUID IDs
- `src/main/java/resource/EscalationResource.java` - Fixed path parameter parsing

## Impact
- **System Loading**: Dramatically improved system startup time by eliminating database errors
- **API Endpoints**: All endpoints now respond correctly instead of throwing database exceptions
- **Data Integrity**: Existing data preserved while adding missing schema elements

## Verification
After implementing the fixes:
1. Server starts without database errors
2. API endpoints return appropriate responses (authentication required instead of database errors)
3. Migration runs automatically on startup
4. No performance degradation during database operations
5. **Escalation functionality works correctly** - tested successfully with POST to `/escalations` endpoint
6. CVE escalations now create proper escalation records with UUID IDs

## Migration Strategy
The fixes were implemented as a new migration (V3) rather than modifying existing migrations to:
- Preserve existing data
- Allow for rollback if needed
- Follow proper database versioning practices 