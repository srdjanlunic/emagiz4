package resource;

import java.util.UUID;
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
        var fileBytes = svc.generateSystemOwnerReport(UUID.fromString(userId));
        return Response.ok(fileBytes)
                .header("Content-Disposition", "attachment; filename=systemowner_report.pdf")
                .header("Content-Type", "application/pdf")
                .build();
    }
    
    @GET
    @Path("/system")
    @RolesAllowed("admin")
    public Response generateSystemsReport() {
        var fileBytes = svc.generateSystemsReport();
        return Response.ok(fileBytes)
                .header("Content-Disposition", "attachment; filename=systems_report.pdf")
                .header("Content-Type", "application/pdf")
                .build();
    }
    
    @GET
    @Path("/vulnerability")
    @RolesAllowed({"security_officer", "admin"})
    public Response generateVulnerabilitiesReport() {
        var fileBytes = svc.generateVulnerabilitiesReport();
        return Response.ok(fileBytes)
                .header("Content-Disposition", "attachment; filename=cve_report.pdf")
                .header("Content-Type", "application/pdf")
                .build();
    }
    
    @GET
    @Path("/escalation")
    @RolesAllowed({"technical_expert", "admin"})
    public Response generateEscalationsReport() {
        var fileBytes = svc.generateEscalationsReport();
        return Response.ok(fileBytes)
                .header("Content-Disposition", "attachment; filename=escalations_report.pdf")
                .header("Content-Type", "application/pdf")
                .build();
    }
    
    @GET
    @Path("/risk-assessment")
    @RolesAllowed({"security_officer", "admin"})
    public Response generateRiskAssessmentReport() {
        var fileBytes = svc.generateRiskAssessmentReport();
        return Response.ok(fileBytes)
                .header("Content-Disposition", "attachment; filename=risk_assessment_report.pdf")
                .header("Content-Type", "application/pdf")
                .build();
    }
}
