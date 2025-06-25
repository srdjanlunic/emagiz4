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

/**
 * REST resource for managing report logs.
 */
@Path("/report-logs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReportLogResource {
    private final ReportLogService svc = new ReportLogService();
    
    // TODO: implement report logs per system owner
    
    /**
     * Returns the timestamp of the last run for a given import type.
     *
     * @param type The import type, e.g. "CVE_IMPORT"
     * @return JSON containing the last import timestamp or an error if the type parameter is missing.
     */
    @GET
    @Path("/last-import")
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
     * Retrieves all report logs.
     *
     * @return List of all ReportLog objects as JSON.
     */
    @GET
    @RolesAllowed({"security_officer", "admin"})
    public Response list() {
        return Response.ok(svc.listAllReportLogs()).build();
    }
    
    /**
     * Creates a new report log.
     *
     * @param rl The ReportLog object parsed from request JSON.
     * @return The created ReportLog with generated ID and HTTP 201 status.
     */
    @POST
    public Response create(ReportLog rl) {
        ReportLog created = svc.logReport(rl);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }
}
