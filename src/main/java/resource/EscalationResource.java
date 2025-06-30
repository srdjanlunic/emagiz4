package resource;

import dto.EscalationCreationDto;
import dto.EscalationReviewDto;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;
import java.util.UUID;
import model.Escalation;
import service.EscalationService;
import util.JsonUtil;

@Path("/escalations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EscalationResource {
    
    private EscalationService svc = new EscalationService();
    
    @POST
    public Response createEscalation(String escalationJson) {
        try {
            var request = JsonUtil.fromJson(escalationJson, EscalationCreationDto.class);
            request.setReason(request.getReason().trim());
            var createdEscalation = svc.create(request);
            
            if (createdEscalation != null) {
                return Response.status(Response.Status.CREATED)
                        .entity(JsonUtil.toJson(createdEscalation))
                        .build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(JsonUtil.toJson(Map.of("error", "Failed to create escalation")))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "Escalation creation failed: " + e.getMessage())))
                    .build();
        }
    }
    
    @PUT
    @Path("/review/{id}")
    public Response reviewEscalation(@PathParam("id") String idStr, String responseJson) {
        try {
            var id = UUID.fromString(idStr);
            var request = JsonUtil.fromJson(responseJson, EscalationReviewDto.class);
            
            request.setResponse(request.getResponse().trim());
            
            var reviewedEscalation = svc.review(id, request);
            
            if (reviewedEscalation != null) {
                return Response.ok(JsonUtil.toJson(reviewedEscalation)).header("Cache-Control", "no-store").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(JsonUtil.toJson(Map.of("error", "Escalation not found")))
                        .header("Cache-Control", "no-store")
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "Escalation review failed: " + e.getMessage())))
                    .header("Cache-Control", "no-store")
                    .build();
        }
    }
    
    @PUT
    @Path("/{id}")
    public Response updateEscalation(@PathParam("id") String idStr, String escalationJson) {
        try {
            var id = UUID.fromString(idStr);
            var escalation = JsonUtil.fromJson(escalationJson, Escalation.class);
            escalation.setId(id);
            
            escalation.setResponse(escalation.getResponse().trim());
            escalation.setEscalationReason(escalation.getEscalationReason().trim());
            
            var updatedEscalation = svc.update(escalation);
            
            
            if (updatedEscalation != null) {
                return Response.ok(JsonUtil.toJson(updatedEscalation)).header("Cache-Control", "no-store").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(JsonUtil.toJson(Map.of("error", "Escalation not found")))
                        .header("Cache-Control", "no-store")
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "Escalation update failed: " + e.getMessage())))
                    .header("Cache-Control", "no-store")
                    .build();
        }
    }
    
    @DELETE
    @Path("/{id}")
    public Response deleteEscalation(@PathParam("id") String idStr) {
        try {
            var id = UUID.fromString(idStr);
            boolean deleted = svc.delete(id);
            if (deleted) {
                return Response.ok(JsonUtil.toJson(Map.of("message", "Escalation deleted successfully"))).header("Cache-Control", "no-store").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(JsonUtil.toJson(Map.of("error", "Escalation not found")))
                        .header("Cache-Control", "no-store")
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "Escalation deletion failed: " + e.getMessage())))
                    .header("Cache-Control", "no-store")
                    .build();
        }
    }
    
    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") String idStr) {
        try {
            var id = UUID.fromString(idStr);
            var escalation = svc.findById(id);
            if (escalation != null) {
                return Response.ok(JsonUtil.toJson(escalation)).header("Cache-Control", "no-store").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(JsonUtil.toJson(Map.of("error", "Escalation not found")))
                        .header("Cache-Control", "no-store")
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "Failed to retrieve escalation: " + e.getMessage())))
                    .header("Cache-Control", "no-store")
                    .build();
        }
    }
    
    @GET
    @Path("/vulnerability/{id}")
    public Response findBySystemVulnerability(@PathParam("id") String idStr) {
        try {
            UUID id = UUID.fromString(idStr);
            var escalation = svc.findBySystemVulnerabilityId(id);
            if (escalation != null) {
                return Response.ok(JsonUtil.toJson(escalation)).header("Cache-Control", "no-store").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(JsonUtil.toJson(Map.of("error", "Escalation not found")))
                        .header("Cache-Control", "no-store")
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "Failed to retrieve escalation: " + e.getMessage())))
                    .header("Cache-Control", "no-store")
                    .build();
        }
    }
    
    @GET
    @Path("/officer/{id}")
    public Response findBySecurityOfficer(@PathParam("id") String idStr) {
        try {
            var id = UUID.fromString(idStr);
            var escalations = svc.findBySecurityOfficer(id);
            return Response.ok(JsonUtil.toJson(escalations)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "Failed to retrieve escalations: " + e.getMessage())))
                    .build();
        }
    }
    
    @GET
    @Path("/expert/{id}")
    public Response findByTechExpert(@PathParam("id") String idStr) {
        try {
            var id = UUID.fromString(idStr);
            var escalations = svc.findByTechExpert(id);
            return Response.ok(JsonUtil.toJson(escalations)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "Failed to retrieve escalations: " + e.getMessage())))
                    .build();
        }
    }
    
    @GET
    public Response findAll() {
        try {
            var escalations = svc.findAll();
            return Response.ok(JsonUtil.toJson(escalations)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "Failed to retrieve escalations: " + e.getMessage())))
                    .build();
        }
    }
}
