package resource;

import jakarta.annotation.security.RolesAllowed;
import service.SettingService;
import util.JsonUtil;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;

/**
 * REST resource for application settings.
 */
@Path("/settings")
@Produces(MediaType.APPLICATION_JSON)
public class SettingResource {
    private SettingService settingService;
    
    /**
     * Default constructor initializing the SettingService.
     */
    public SettingResource() {
        this.settingService = new SettingService();
    }
    
    /**
     * Retrieves the value of a configuration setting by key.
     *
     * @param key the setting key to retrieve
     * @return HTTP 200 with JSON containing the key and value if found,
     *         or HTTP 404 with error message if not found
     */
    @GET
    @Path("/{key}")
    @RolesAllowed({"security_officer", "technical_expert", "admin"})
    public Response getSetting(@PathParam("key") String key) {
        String value = settingService.getSetting(key);
        if (value != null) {
            return Response.ok(JsonUtil.toJson(Map.of("key", key, "value", value))).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(JsonUtil.toJson(Map.of("error", "Setting not found")))
                    .build();
        }
    }
}

