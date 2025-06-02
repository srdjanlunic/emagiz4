package config;

import org.glassfish.jersey.server.ResourceConfig;
import resource.*;
import security.CorsFilter;

public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        // Register REST resources
        register(AuthResource.class);
        register(UserResource.class);
        register(SystemResource.class);
        register(VulnerabilityResource.class);
        register(NotificationResource.class);
        register(HealthResource.class);  // Make sure HealthResource is registered

        // Register filters
        register(CorsFilter.class);

        // Register Jackson JSON provider
        packages("com.fasterxml.jackson.jaxrs.json");
    }
}
