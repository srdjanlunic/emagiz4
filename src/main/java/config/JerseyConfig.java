package config;

import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;
import resource.*;
import security.AuthFilter;
import security.CorsFilter;

/**
 * Configures Jersey REST endpoints under "/api".
 */
@ApplicationPath("/api")
public class JerseyConfig extends ResourceConfig {
    /**
     * Registers all API resources and filters.
     */
    public JerseyConfig() {
        // === Resources ===
        register(HealthResource.class);
        register(AuthResource.class);
        register(UserResource.class);
        register(SystemResource.class);
        register(VulnerabilityResource.class);
        register(NotificationResource.class);
        register(DepartmentResource.class); // Department endpoints
        
        // === Filters ===
        register(AuthFilter.class);      // Security: check authentication
        register(CorsFilter.class);      // Handle CORS headers
        
        // Support @RolesAllowed annotations on resource methods
        register(org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature.class);
    }
}
