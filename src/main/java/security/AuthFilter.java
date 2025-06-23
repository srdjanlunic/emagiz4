package security;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.security.Principal;
import java.util.Collections;
import java.util.List;

@Provider
@PreMatching
public class AuthFilter implements ContainerRequestFilter {

    private static final String AUTH_SCHEME = "Bearer";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        
        System.out.println();
        
        // Handle CORS preflight requests
        if (requestContext.getRequest().getMethod().equals("OPTIONS")) {
            requestContext.abortWith(Response.status(Response.Status.OK).build());
            return;
        }

        // Skip authentication for auth endpoints and health check
        String path = requestContext.getUriInfo().getPath();
        System.out.println(path);
        if (path.contains("auth") || path.endsWith("/health")) {
            return;
        }

        // Get the Authorization header from the request
        String authHeader = requestContext.getHeaderString("Authorization");
        
        System.out.println("Before auth header check");
        // Validate the Authorization header
        if (authHeader == null || !authHeader.startsWith(AUTH_SCHEME + " ")) {
            abortWithUnauthorized(requestContext, "Authorization header must be provided");
            return;
        }
        System.out.println("After auth header check");
        // Extract the token from the Authorization header
        final String token = authHeader.substring(AUTH_SCHEME.length()).trim();

        try {
            // Validate the token
            if (JWTUtil.validateToken(token)) {
                // Extract user details from the token
                final String username = JWTUtil.getUsernameFromToken(token);
                final String role = JWTUtil.getRoleFromToken(token);
                System.out.println(role);
                final List<String> roles = (role != null) ? Collections.singletonList(role) : Collections.emptyList();

                // Create a new SecurityContext
                SecurityContext originalContext = requestContext.getSecurityContext();
                requestContext.setSecurityContext(new SecurityContext() {
                    @Override
                    public Principal getUserPrincipal() {
                        return () -> username;
                    }

                    @Override
                    public boolean isUserInRole(String requiredRole) {
                        if (roles.isEmpty()) {
                            return false;
                        }
                        // Case-insensitive role check
                        return roles.stream().anyMatch(r -> r.equalsIgnoreCase(requiredRole));
                    }

                    @Override
                    public boolean isSecure() {
                        return originalContext.isSecure();
                    }

                    @Override
                    public String getAuthenticationScheme() {
                        return AUTH_SCHEME;
                    }
                });
            } else {
                abortWithUnauthorized(requestContext, "Invalid token");
            }
        } catch (Exception e) {
            abortWithUnauthorized(requestContext, "Token validation failed");
        }
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext, String message) {
        requestContext.abortWith(
            Response.status(Response.Status.UNAUTHORIZED)
                    .header("WWW-Authenticate", AUTH_SCHEME)
                    .entity("{\"error\":\"" + message + "\"}")
                    .build()
        );
    }
}
