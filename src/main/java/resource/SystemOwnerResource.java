// resource/SystemOwnerResource.java
package resource;

import model.SystemImplementation;
import model.User;
import service.SystemOwnerService;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Path("/owners")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SystemOwnerResource {

    private final SystemOwnerService svc = new SystemOwnerService();

    @POST
    @RolesAllowed("security_officer")
    public Response assign(Map<String,String> body) {
        UUID userId = UUID.fromString(body.get("userId"));
        UUID implId = UUID.fromString(body.get("implementationId"));
        svc.assignOwner(userId, implId);
        return Response.status(Response.Status.CREATED)
                .entity(Map.of("message","Owner assigned"))
                .build();
    }

    @DELETE
    @Path("/{userId}/{implId}")
    @RolesAllowed("security_officer")
    public Response unassign(@PathParam("userId") String u, @PathParam("implId") String s) {
        UUID userId = UUID.fromString(u);
        UUID implId = UUID.fromString(s);
        boolean ok = svc.removeOwner(userId, implId);
        if (!ok) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error","Assignment not found"))
                    .build();
        }
        return Response.ok(Map.of("message","Owner unassigned")).build();
    }

    @GET
    @Path("/implementation/{id}")
    @RolesAllowed({"security_officer","system_owner"})
    public Response ownersOfImplementation(@PathParam("id") String idStr) {
        UUID implId = UUID.fromString(idStr);
        List<User> list = svc.getOwnersForImplementation(implId);
        return Response.ok(list).build();
    }

    @GET
    @Path("/user/{id}")
    @RolesAllowed({"security_officer","system_owner"})
    public Response implementationsOfOwner(@PathParam("id") String idStr) {
        UUID userId = UUID.fromString(idStr);
        List<SystemImplementation> list = svc.getImplementationsForOwner(userId);
        return Response.ok(list).build();
    }
}
