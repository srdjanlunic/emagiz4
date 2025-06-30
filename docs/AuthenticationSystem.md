# Authentication System

## Overview
The authentication system handles user login, role-based access control, and session management using JWT tokens.

## Components

### Backend (Java)
- **AuthResource.java**: REST endpoints for login, logout, validation
- **AuthService.java**: Business logic for authentication and role lookup
- **UserDAO.java**: Database operations for user management
- **RoleDAO.java**: Database operations for role management
- **JWTUtil.java**: JWT token generation and validation

### Frontend (Vue.js)
- **auth.js**: Pinia store for authentication state management
- **router/index.js**: Navigation guards for route protection

## Role System

### Role Types
- **ADMIN**: System administrator with full access
- **SYSTEM_OWNER**: Manages system implementations and assesses vulnerabilities
- **SECURITY_OFFICER**: Oversees security operations and vulnerability management  
- **TECHNICAL_EXPERT**: Provides technical expertise on vulnerabilities and systems

### Role ID Mapping
Due to database migration compatibility, the system handles both integer and UUID role IDs:

**Integer to UUID Mapping:**
- 1 → `a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15` (ADMIN)
- 2 → `a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12` (SYSTEM_OWNER)
- 3 → `a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13` (SECURITY_OFFICER)
- 4 → `a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14` (TECHNICAL_EXPERT)

## Authentication Flow

1. User submits credentials to `/api/auth/login`
2. AuthService validates username/password against database
3. If valid, JWT token is generated with user info
4. Role information is fetched and mapped to proper format
5. Token and user data returned to frontend
6. Frontend stores token in localStorage and user data in Pinia store
7. Navigation guards check authentication and role permissions

## Troubleshooting

### "User role: UNKNOWN" Issue
This typically indicates a role mapping problem. Check:

1. **Database Migration Status**: Ensure V4 migration has run properly
2. **Role Data**: Verify role table contains proper entries
3. **User Role Assignment**: Check user's role_id is valid

**Fixed by:**
- UserDAO.mapResultSetToUser() handles both integer and UUID role IDs
- RoleDAO.findById() includes fallback integer mapping
- Frontend auth store includes comprehensive role name mapping

### Infinite Navigation Redirect
Caused by navigation guards not handling UNKNOWN roles properly.

**Fixed by:**
- Router navigation guard detects UNKNOWN role and redirects to login
- Auth store clears invalid sessions
- Improved role mapping in login process

### 403 Forbidden Errors
Result of UNKNOWN role not having proper permissions.

**Fixed by:**
- Proper role mapping ensures valid role names
- Navigation guards prevent access with invalid roles

## Test Users

From V2 migration data:
- **admin/admin123**: ADMIN role
- **sysowner/owner123**: SYSTEM_OWNER role  
- **secofficer/officer123**: SECURITY_OFFICER role
- **techexpert/expert123**: TECHNICAL_EXPERT role

## API Endpoints

- `POST /api/auth/login`: User login
- `POST /api/auth/logout`: User logout
- `GET /api/auth/validate`: Token validation
- `POST /api/auth/demo-login`: Demo login by role name

## Known Issues

- Role table may use integer IDs instead of UUIDs depending on migration state
- Some legacy data may require manual cleanup
- Demo login functionality depends on proper role data setup

## Key Components

### AuthService
- Handles user authentication logic
- Validates username and password format
- Performs plaintext password comparison with database stored passwords
- Generates JWT tokens for authenticated users
- Retrieves user roles and permissions

### AuthResource (REST Endpoint)
- Provides `/api/auth/login` endpoint for user authentication
- Accepts JSON payload with username and password
- Returns JWT token and user details on successful authentication
- Handles demo login functionality for testing

### UserDAO & RoleDAO
- **UserDAO**: Manages user data access from `useraccount` table
- **RoleDAO**: Manages role data access from `role` table
- Uses PostgreSQL with quoted table names for case sensitivity
- Role IDs are integers (SERIAL PRIMARY KEY), not UUIDs

### JWT Token Management
- Uses JWTUtil for token generation and validation
- Tokens contain user ID, username, role ID, and role name
- 24-hour token expiration time
- HS256 algorithm for signing

## Database Schema

### Users Table (`useraccount`)
- `id`: UUID (Primary Key)
- `username`: Text (Unique)
- `email`: Text (Unique)
- `password`: Text (Plaintext for demo)
- `role_id`: Integer (Foreign Key to role table)
- `organization_id`: UUID (Foreign Key)
- `is_active`: Boolean

### Roles Table (`role`)
- `id`: Serial (Primary Key)
- `name`: Text (Unique)
- `created_at`: Timestamp

## Available Demo Accounts

| Username | Password | Role ID | Role Name |
|----------|----------|---------|-----------|
| admin | admin123 | 1 | admin |
| sysowner | owner123 | 2 | system_owner |
| secofficer | officer123 | 3 | security_officer |
| techexpert | expert123 | 4 | technical_expert |

## API Endpoints

### POST /api/auth/login
**Request:**
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": "8f9b76c2-5e41-4c44-9e87-7a8d2da0a386",
    "username": "admin",
    "roleId": "1",
    "roleName": "ADMIN",
    "organizationId": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11"
  }
}
```

### POST /api/auth/demo-login
**Request:**
```json
{
  "roleName": "admin"
}
```

**Response:** Same as regular login

### GET /api/auth/validate
**Headers:**
```
Authorization: Bearer <token>
```

**Response:**
```json
{
  "valid": true,
  "username": "admin",
  "userId": "8f9b76c2-5e41-4c44-9e87-7a8d2da0a386",
  "roleId": "1"
}
```

## Frontend Integration

### Login Component (`src/views/auth/Login.vue`)
- Provides form for username/password input
- Includes demo login buttons for quick testing
- Handles authentication errors and loading states
- Redirects to dashboard on successful login

### Auth Store (`src/stores/auth.js`)
- Manages authentication state in Pinia store
- Stores JWT token and user details in localStorage
- Provides authentication methods and getters
- Handles token validation and logout

## Security Considerations

⚠️ **Note**: This system uses plaintext passwords for demo purposes only. In production:
- Implement password hashing (BCrypt)
- Add password complexity requirements
- Implement rate limiting for login attempts
- Add CSRF protection
- Use HTTPS only
- Implement proper session management

## Troubleshooting

### Common Issues
1. **500 Server Error**: Check database connection and table case sensitivity
2. **Invalid Credentials**: Verify username/password match database exactly
3. **Token Validation Failed**: Check token expiration and signature

### Debug Logging
The AuthService includes console logging for debugging:
- Authentication attempts
- User lookup results
- Password comparison results
- Token generation status

## Role-Based Access Control

The system supports four main roles:
- **Admin**: Full system access
- **System Owner**: Manage assigned systems
- **Security Officer**: Review and assess vulnerabilities
- **Technical Expert**: Provide technical expertise on vulnerabilities

Role permissions are handled at the frontend level through the auth store getters.

## System Status: ✅ **FULLY OPERATIONAL**

**Last Updated**: June 30, 2025  
**Status**: All authentication issues resolved - system fully functional

### ✅ **Resolved Issues Summary**

All previous authentication problems have been **completely fixed**:

1. **500 Server Error on Login** ✅ **RESOLVED**
2. **Role Name Showing "UNKNOWN"** ✅ **RESOLVED** 
3. **403 Forbidden Errors** ✅ **RESOLVED**
4. **Infinite Vue Router Redirects** ✅ **RESOLVED**
5. **System Creation Functionality** ✅ **RESOLVED**

### 🧪 **Latest Test Results** (June 30, 2025)

**Backend API Testing:**
```bash
✅ Admin Login: Returns JWT with role "ADMIN"
✅ System Owner Login: Returns JWT with role "SYSTEM_OWNER" 
✅ Security Officer Login: Returns JWT with role "SECURITY_OFFICER"
✅ Technical Expert Login: Returns JWT with role "TECHNICAL_EXPERT"
✅ Protected Endpoints: All accessible with proper JWT tokens
✅ System Creation: Successfully creates new systems via API
```

**JWT Token Validation:**
```json
{
  "sub": "admin",
  "userId": "8f9b76c2-5e41-4c44-9e87-7a8d2da0a386",
  "roleId": "1", 
  "role": "ADMIN",
  "iat": 1751285935,
  "exp": 1751372335
}
✅ Role field found in JWT token: ADMIN
```

**Frontend Integration:**
- ✅ Login page: Fully functional with demo buttons
- ✅ Role-based navigation: Working for all user types
- ✅ Dashboard access: Available after authentication
- ✅ System management: Add/edit/view systems working
- ✅ Protected routes: Proper access control enforced 