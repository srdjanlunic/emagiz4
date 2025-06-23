package resource;

import service.ReportService;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

@Path("/reports")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReportResource {
    
    //TODO: filter by assigned systems
    
    private final ReportService svc = new ReportService();
    
    /**
     * Generates the dashboard summary.
     * Only security officers may call this.
     */
    @POST
    @Path("/dashboard")
    @RolesAllowed({"security_officer", "admin"})
    public Response dashboard() {
        // svc.generateDashboard() returns Map<UUID, Map<String,Long>>
        return Response.ok(svc.generateDashboard()).build();
    }
}
