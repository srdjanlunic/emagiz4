package resource;

import service.SystemService;
import model.ITSystem;
import util.JsonUtil;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.Map;

@Path("/systems")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SystemResource {
    private SystemService systemService;

    public SystemResource() {
        this.systemService = new SystemService();
    }

    // register system (security officer only)
    @POST
    @RolesAllowed({"security_officer"})
    public Response registerSystem(String systemJson) {
        try {
            ITSystem system = JsonUtil.fromJson(systemJson, ITSystem.class);
            ITSystem createdSystem = systemService.createSystem(system);

            if (createdSystem != null) {
                return Response.status(Response.Status.CREATED)
                        .entity(JsonUtil.toJson(createdSystem))
                        .build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(JsonUtil.toJson(Map.of("error", "Failed to create system")))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "System registration failed: " + e.getMessage())))
                    .build();
        }
    }

    // get all systems
    @GET
    @RolesAllowed({"security_officer", "system_owner"})
    public Response getAllSystems() {
        try {
            List<ITSystem> systems = systemService.getAllSystems();
            return Response.ok(JsonUtil.toJson(systems)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "Failed to retrieve systems: " + e.getMessage())))
                    .build();
        }
    }

    // get system by id
    @GET
    @Path("/{id}")
    @RolesAllowed({"security_officer", "system_owner"})
    public Response getSystemById(@PathParam("id") Long id) {
        try {
            ITSystem system = systemService.getSystemById(id);
            if (system != null) {
                return Response.ok(JsonUtil.toJson(system)).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(JsonUtil.toJson(Map.of("error", "System not found")))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "Failed to retrieve system: " + e.getMessage())))
                    .build();
        }
    }

    // update system
    @PUT
    @Path("/{id}")
    @RolesAllowed({"security_officer"})
    public Response updateSystem(@PathParam("id") Long id, String systemJson) {
        try {
            ITSystem system = JsonUtil.fromJson(systemJson, ITSystem.class);
            system.setId(id);
            ITSystem updatedSystem = systemService.updateSystem(system);

            if (updatedSystem != null) {
                return Response.ok(JsonUtil.toJson(updatedSystem)).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(JsonUtil.toJson(Map.of("error", "System not found")))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "System update failed: " + e.getMessage())))
                    .build();
        }
    }

    // delete system
    @DELETE
    @Path("/{id}")
    @RolesAllowed({"security_officer"})
    public Response deleteSystem(@PathParam("id") Long id) {
        try {
            boolean deleted = systemService.deleteSystem(id);
            if (deleted) {
                return Response.ok(JsonUtil.toJson(Map.of("message", "System deleted successfully"))).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(JsonUtil.toJson(Map.of("error", "System not found")))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "System deletion failed: " + e.getMessage())))
                    .build();
        }
    }

    // get systems by owner
    @GET
    @Path("/owner/{ownerId}")
    @RolesAllowed({"system_owner", "security_officer"})
    public Response getSystemsByOwner(@PathParam("ownerId") Long ownerId) {
        try {
            List<ITSystem> systems = systemService.getSystemsByOwner(ownerId);
            return Response.ok(JsonUtil.toJson(systems)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "Failed to retrieve systems by owner: " + e.getMessage())))
                    .build();
        }
    }
}
