# SystemResource API Documentation

## Overview
The SystemResource provides REST endpoints for managing IT systems and their implementations within the vulnerability management system. This resource handles CRUD operations for systems and includes specialized endpoints for role-based access control.

## Base Path
`/api/systems`

## Security
All endpoints require authentication via JWT Bearer token. Role-based access control is enforced using `@RolesAllowed` annotations.

## Endpoints

### GET /systems/owner/{ownerId}
**Purpose**: Retrieves all systems owned by a specific user.

**Access Control**: `security_officer`, `system_owner`, `admin`

**Parameters**:
- `ownerId` (path): UUID string of the owner (user) to get systems for

**Response**: 
- 200: List of SystemDto objects representing systems owned by the user
- 500: Internal server error with error message
- 401: Unauthorized if token is invalid
- 403: Forbidden if user lacks required role

**Usage**: This endpoint is primarily used by system owners to view their assigned systems. The frontend calls this endpoint when a system owner logs in to populate their systems dashboard.

**Example Response**:
```json
[
  {
    "id": "uuid-string",
    "name": "Production Web Server",
    "vendor": "Apache",
    "version": "2.4.41",
    "riskScore": 75,
    "criticalityLevel": "HIGH",
    "ownerId": "owner-uuid",
    "createdAt": "2024-01-01T00:00:00Z"
  }
]
```

### Other Endpoints
- `GET /systems` - Retrieve all systems (paginated)
- `GET /systems/{id}` - Retrieve system by ID
- `POST /systems` - Create new system
- `PUT /systems/{id}` - Update system
- `DELETE /systems/{id}` - Delete system
- Various `/implementations` endpoints for managing system implementations

## Known Issues
- Fixed: Missing `/systems/owner/{ownerId}` endpoint was causing 404 errors for system owners trying to view their systems
- The endpoint now properly integrates with SystemOwnerDAO to fetch user-owned systems

## Dependencies
- SystemService for business logic
- SystemOwnerDAO for ownership queries
- JsonUtil for JSON serialization
- JWT authentication filter 