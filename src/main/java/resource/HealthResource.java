package resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Resource to provide health check endpoint for the API.
 */
@Path("/health")
public class HealthResource {
    
    /**
     * Handles GET requests to check the health status of the API.
     *
     * @return JSON response indicating the API is running with status and timestamp
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        health.put("message", "API is running properly");
        
        return Response.ok(health).build();
    }
}
