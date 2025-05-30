package config;

import org.glassfish.jersey.server.ResourceConfig;
import resource.*;

public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        // register REST resources
        register(AuthResource.class);
        register(UserResource.class);
        register(SystemResource.class);
        register(VulnerabilityResource.class);
        register(NotificationResource.class);

        // register filters
        // TODO: register authentication filter

        // register exception mappers
        // TODO: register exception mappers
    }
}
