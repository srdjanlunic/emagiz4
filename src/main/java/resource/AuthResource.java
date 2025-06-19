package resource;

import jakarta.annotation.security.RolesAllowed;
import service.AuthService;
import model.User;
import security.JWTUtil;
import util.JsonUtil;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import model.Role;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {
    private AuthService authService;

    public AuthResource() {
        this.authService = new AuthService();
    }

    // login endpoint
    @POST
    @Path("/login")
    public Response login(LoginRequest loginRequest) {
        try {
            if (loginRequest.username == null || loginRequest.password == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(JsonUtil.toJson(Map.of("error", "Username and password are required")))
                        .header("Cache-Control", "no-store")
                        .build();
            }

            User user = authService.authenticate(loginRequest.username, loginRequest.password);
            if (user != null) {
                String token = JWTUtil.generateToken(user);
                Role role = authService.getRoleByUser(user);

                System.out.println("User '" + user.getUsername() + "' logged in with role: " + role.getName().toUpperCase());

                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("user", Map.of(
                        "id", user.getId().toString(),
                        "username", user.getUsername(),
                        "roleId", user.getRoleId() != null ? user.getRoleId().toString() : "",
                        "roleName", role.getName().toUpperCase(),
                        "organizationId", user.getOrganizationId() != null ? user.getOrganizationId().toString() : ""
                ));

                return Response.ok(JsonUtil.toJson(response)).header("Cache-Control", "no-store").build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(JsonUtil.toJson(Map.of("error", "Invalid credentials")))
                        .header("Cache-Control", "no-store")
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "Login failed: " + e.getMessage())))
                    .header("Cache-Control", "no-store")
                    .build();
        }
    }

    // logout endpoint
    @POST
    @Path("/logout")
    public Response logout() {
        // For JWT, logout is handled client-side by removing the token
        return Response.ok(JsonUtil.toJson(Map.of("message", "Logged out successfully"))).header("Cache-Control", "no-store").build();
    }

    // validate token endpoint
    @GET
    @Path("/validate")
    public Response validateToken(@HeaderParam("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(JsonUtil.toJson(Map.of("error", "Missing or invalid authorization header")))
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

                return Response.ok(JsonUtil.toJson(response)).header("Cache-Control", "no-store").build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(JsonUtil.toJson(Map.of("error", "Invalid or expired token")))
                        .header("Cache-Control", "no-store")
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(JsonUtil.toJson(Map.of("error", "Token validation failed")))
                    .header("Cache-Control", "no-store")
                    .build();
        }
    }

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

                return Response.ok(JsonUtil.toJson(response)).header("Cache-Control", "no-store").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(JsonUtil.toJson(Map.of("error", "No user found for role: " + request.roleName)))
                        .header("Cache-Control", "no-store")
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "Demo login failed: " + e.getMessage())))
                    .header("Cache-Control", "no-store")
                    .build();
        }
    }

    // inner class for login request
    public static class LoginRequest {
        public String username;
        public String password;
    }

    public static class DemoLoginRequest {
        public String roleName;
    }
}
