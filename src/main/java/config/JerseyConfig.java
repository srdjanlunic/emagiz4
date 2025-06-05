package config;

import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;
import resource.*;
import security.AuthFilter;
import security.CorsFilter;

@ApplicationPath("/api")
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        // Register resources
        register(HealthResource.class);
        register(AuthResource.class);
        register(UserResource.class);
        register(SystemResource.class);
        register(VulnerabilityResource.class);
        register(NotificationResource.class);
        register(DepartmentResource.class); // Make sure this is registered

        // Register filters
        register(AuthFilter.class);
        register(CorsFilter.class);
    }
}
