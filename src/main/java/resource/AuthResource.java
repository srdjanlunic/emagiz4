package resource;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import model.Role;
import model.User;
import service.AuthService;
import security.JWTUtil;
import util.JsonUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * REST resource for authentication operations.
 */
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {
    private AuthService authService;
    
    /**
     * Default constructor initializes AuthService.
     */
    public AuthResource() {
        this.authService = new AuthService();
    }
    
    /**
     * Endpoint to log in a user.
     *
     * @param loginRequest the login request containing username and password
     * @return JSON response containing JWT token and user details or error message
     */
    @POST
    @Path("/login")
    @PermitAll
    public Response login(LoginRequest loginRequest) {
        try {
            System.out.println("Login attempt for username: " + loginRequest.username);
            
            if (loginRequest.username == null || loginRequest.password == null) {
                System.out.println("Missing username or password");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Username and password are required"))
                        .header("Cache-Control", "no-store")
                        .build();
            }
            
            User user = authService.authenticate(loginRequest.username, loginRequest.password);
            if (user != null) {
                System.out.println("User authenticated successfully: " + user.getUsername());
                String token = JWTUtil.generateToken(user);
                Role role = authService.getRoleByUser(user);
                
                String roleName = (role != null) ? role.getName().toLowerCase() : "UNKNOWN";
                
                // Fallback: if role lookup failed, use direct mapping like in JWTUtil
                if (role == null && user.getRoleId() != null) {
                    String roleIdStr = user.getRoleId().toString();
                    switch (roleIdStr) {
                        case "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15":
                            roleName = "admin";
                            break;
                        case "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12":
                            roleName = "system_owner";
                            break;
                        case "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13":
                            roleName = "security_officer";
                            break;
                        case "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14":
                            roleName = "technical_expert";
                            break;
                        default:
                            roleName = "UNKNOWN";
                    }
                }
                
                String roleIdStr = (user.getRoleId() != null) ? user.getRoleId().toString() : "";
                
                System.out.println("User '" + user.getUsername() + "' logged in with role: " + roleName);
                
                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("user", Map.of(
                        "id", user.getId().toString(),
                        "username", user.getUsername(),
                        "roleId", roleIdStr,
                        "roleName", roleName,
                        "organizationId", user.getOrganizationId() != null ? user.getOrganizationId().toString() : ""
                ));
                
                return Response.ok(response).header("Cache-Control", "no-store").build();
            } else {
                System.out.println("Authentication failed for username: " + loginRequest.username);
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(Map.of("error", "Invalid credentials"))
                        .header("Cache-Control", "no-store")
                        .build();
            }
        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Login failed: " + e.getMessage()))
                    .header("Cache-Control", "no-store")
                    .build();
        }
    }
    
    /**
     * Endpoint to log out a user.
     *
     * Note: For JWT, logout is handled client-side by removing the token.
     *
     * @return JSON response confirming logout
     */
    @POST
    @Path("/logout")
    public Response logout() {
        return Response.ok(Map.of("message", "Logged out successfully"))
                .header("Cache-Control", "no-store")
                .build();
    }
    
    /**
     * Endpoint to validate a JWT token.
     *
     * @param authHeader the Authorization header containing the Bearer token
     * @return JSON response indicating token validity or error message
     */
    @GET
    @Path("/validate")
    public Response validateToken(@HeaderParam("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(Map.of("error", "Missing or invalid authorization header"))
                        .header("Cache-Control", "no-store")
                        .build();
            }
            
            String token = authHeader.substring(7);
            if (JWTUtil.validateToken(token)) {
                String username = JWTUtil.getUsernameFromToken(token);
                String userId = JWTUtil.getUserIdFromToken(token).toString();
                String roleId = JWTUtil.getRoleIdFromToken(token) != null ?
                        JWTUtil.getRoleIdFromToken(token).toString() : "";
                
                Map<String, Object> response = Map.of(
                        "valid", true,
                        "username", username,
                        "userId", userId,
                        "roleId", roleId
                );
                
                return Response.ok(response).header("Cache-Control", "no-store").build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(Map.of("error", "Invalid or expired token"))
                        .header("Cache-Control", "no-store")
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("error", "Token validation failed"))
                    .header("Cache-Control", "no-store")
                    .build();
        }
    }
    
    /**
     * Demo login endpoint to login by role name (for testing/demo purposes).
     *
     * @param request the demo login request containing the role name
     * @return JSON response with token and user details or error message
     */
    @POST
    @Path("/demo-login")
    public Response demoLogin(DemoLoginRequest request) {
        try {
            User user = authService.findUserByRole(request.roleName);
            if (user != null) {
                String token = JWTUtil.generateToken(user);
                Role role = authService.getRoleByUser(user);
                
                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("user", Map.of(
                        "id", user.getId().toString(),
                        "username", user.getUsername(),
                        "roleId", user.getRoleId() != null ? user.getRoleId().toString() : "",
                        "roleName", role.getName().toUpperCase(),
                        "organizationId", user.getOrganizationId() != null ? user.getOrganizationId().toString() : ""
                ));
                
                return Response.ok(response).header("Cache-Control", "no-store").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("error", "No user found for role: " + request.roleName))
                        .header("Cache-control", "no-store")
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Demo login failed: " + e.getMessage()))
                    .header("Cache-Control", "no-store")
                    .build();
        }
    }
    
    /**
     * Test endpoint to check role data in database.
     *
     * @return JSON response with role information
     */
    @GET
    @Path("/test-roles")
    @PermitAll
    public Response testRoles() {
        try {
            dao.RoleDAO roleDAO = new dao.RoleDAO();
            java.util.List<model.Role> roles = roleDAO.findAll();
            
            Map<String, Object> response = new HashMap<>();
            response.put("totalRoles", roles.size());
            response.put("roles", roles.stream().map(role -> Map.of(
                "id", role.getId(),
                "name", role.getName(),
                "description", role.getDescription() != null ? role.getDescription() : "No description"
            )).collect(java.util.stream.Collectors.toList()));
            
            // Test finding role by name
            model.Role systemOwnerRole = roleDAO.findByName("SYSTEM_OWNER");
            response.put("systemOwnerRoleLookup", systemOwnerRole != null ? 
                Map.of("id", systemOwnerRole.getId(), "name", systemOwnerRole.getName()) : "null");
            
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Test failed: " + e.getMessage()))
                    .build();
        }
    }
    
    /**
     * Inner class representing login request payload.
     */
    public static class LoginRequest {
        public String username;
        public String password;
    }
    
    /**
     * Inner class representing demo login request payload.
     */
    public static class DemoLoginRequest {
        public String roleName;
    }
}
