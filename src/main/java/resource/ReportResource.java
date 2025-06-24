package resource;

import service.ReportService;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.*;
import service.RoleService;
import service.UserService;

@Path("/reports")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReportResource {
    
    //TODO: filter by assigned systems
    
    private final ReportService svc = new ReportService();
    private final UserService userSvc = new UserService();
    private final RoleService roleSvc = new RoleService();
    
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
    
    @GET
    @Path("/system/{userId}")
    @RolesAllowed("system_owner")
    public Response generateSystemOwnerReport(@PathParam("userId") String userId) {
        return null;
    }
    
    @GET
    @Path("/system")
    @RolesAllowed("admin")
    public Response generateSystemsReport() {
        return null;
    }
    
    @GET
    @Path("/vulnerability")
    @RolesAllowed({"security_officer", "admin"})
    public Response generateVulnerabilitiesReport() {
        return null;
    }
    
    @GET
    @Path("/escalation")
    @RolesAllowed({"technical_expert", "admin"})
    public Response generateEscalationsReport() {
        return null;
    }
}
