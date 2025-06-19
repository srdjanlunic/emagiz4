package resource;

import model.ReportLog;
import service.ReportLogService;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;

@Path("/report-logs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReportLogResource {
    private final ReportLogService svc = new ReportLogService();

    /**
     * GET /report-logs/last-import?type=CVE_IMPORT
     */
    @GET
    @Path("/last-import")
    ////@RolesAllowed({"security_officer", "admin"})
    public Response lastImport(@QueryParam("type") String type) {
        if (type == null || type.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error","Query-param 'type' required"))
                    .build();
        }
        Timestamp ts = svc.getLastRun(type);
        return Response.ok(Map.of("lastImport", ts)).build();
    }

    /**
     * GET /report-logs
     */
    @GET
    ////@RolesAllowed({"security_officer", "admin"})
    public Response list() {
        return Response.ok(svc.listAllReportLogs()).build();
    }

    /**
     * POST /report-logs
     * Body is a full ReportLog JSON; returns created record with id.
     */
    @POST
    ////@RolesAllowed({"security_officer", "admin"})
    public Response create(ReportLog rl) {
        ReportLog created = svc.logReport(rl);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }
}
