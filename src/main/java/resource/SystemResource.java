package resource;

import service.SystemService;
import model.ITSystem;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.annotation.security.RolesAllowed;

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
    public Response registerSystem(ITSystem system) {
        // TODO: register new system
        // TODO: return created system or error
        return null;
    }

    // get all systems
    @GET
    @RolesAllowed({"security_officer", "system_owner"})
    public Response getAllSystems() {
        // TODO: get all systems
        // TODO: return system list
        return null;
    }

    // get system by id
    @GET
    @Path("/{id}")
    @RolesAllowed({"security_officer", "system_owner"})
    public Response getSystemById(@PathParam("id") Long id) {
        // TODO: get system by id
        // TODO: return system or error
        return null;
    }

    // update system
    @PUT
    @Path("/{id}")
    @RolesAllowed({"security_officer"})
    public Response updateSystem(@PathParam("id") Long id, ITSystem system) {
        // TODO: update system
        // TODO: return updated system or error
        return null;
    }

    // delete system
    @DELETE
    @Path("/{id}")
    @RolesAllowed({"security_officer"})
    public Response deleteSystem(@PathParam("id") Long id) {
        // TODO: delete system
        // TODO: return success or error
        return null;
    }

    // get systems by owner
    @GET
    @Path("/owner/{ownerId}")
    @RolesAllowed({"system_owner", "security_officer"})
    public Response getSystemsByOwner(@PathParam("ownerId") Long ownerId) {
        // TODO: get systems by owner
        // TODO: return system list
        return null;
    }
}
