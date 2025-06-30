# Authentication System

## Overview
The CVE Management System uses a JWT-based authentication system for user login and session management. The system handles user authentication through plaintext password comparison (for demo purposes) and generates JWT tokens for subsequent API requests.

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