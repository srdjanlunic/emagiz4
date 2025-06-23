package resource;

import jakarta.annotation.security.RolesAllowed;
import model.ITSystem;
import model.SystemImplementation;
import service.SystemImplementationService;
import service.SystemService;
import util.JsonUtil;
import dto.SystemDto;
import dto.SystemImplementationDto;
import dao.SystemImplementationDAO;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/systems")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SystemResource {
    private final SystemService systemService;
    private final SystemImplementationService systemImplementationService;
    private final SystemImplementationDAO systemImplementationDAO;
    private static final Logger logger = LoggerFactory.getLogger(SystemResource.class);

    public SystemResource() {
        this.systemService = new SystemService();
        this.systemImplementationService = new SystemImplementationService();
        this.systemImplementationDAO = new SystemImplementationDAO();
    }
    
    //TODO: implement linking systems to system owners
    
    @POST
    @RolesAllowed({"system_owner", "admin"})
    public Response createSystem(String systemJson) {
        logger.info("Received request to create system with JSON: {}", systemJson);
        try {
            SystemDto systemDto = JsonUtil.fromJson(systemJson, SystemDto.class);
            logger.info("Successfully parsed SystemDto: {}", systemDto);
            ITSystem createdSystem = systemService.createSystem(systemDto);
            logger.info("Successfully created system: {}", createdSystem);
            return Response.status(Response.Status.CREATED).entity(JsonUtil.toJson(createdSystem)).build();
        } catch (Exception e) {
            logger.error("Error creating system", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @GET
    @RolesAllowed({"security_officer", "system_owner", "admin"})
    public Response getAllSystems() {
        try {
            List<SystemDto> systems = systemService.getAllSystems();
            return Response.ok(JsonUtil.toJson(systems)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"security_officer", "system_owner", "admin"})
    public Response getSystemById(@PathParam("id") String idStr) {
        try {
            UUID id = UUID.fromString(idStr);
            SystemDto system = systemService.getSystemById(id);
            if (system != null) {
                return Response.ok(JsonUtil.toJson(system)).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"system_owner", "admin"})
    public Response updateSystem(@PathParam("id") String idStr, String systemJson) {
        try {
            UUID id = UUID.fromString(idStr);
            SystemDto systemDto = JsonUtil.fromJson(systemJson, SystemDto.class);
            ITSystem updatedSystem = systemService.updateSystem(id, systemDto);
            if (updatedSystem != null) {
                return Response.ok(JsonUtil.toJson(updatedSystem)).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"system_owner", "admin"})
    public Response deleteSystem(@PathParam("id") String idStr) {
        try {
            UUID id = UUID.fromString(idStr);
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
                    .entity(JsonUtil.toJson(Map.of("error", "System deletion failed")))
                    .build();
        }
    }

    @POST
    @Path("/implementations")
    @RolesAllowed({"system_owner", "admin"})
    public Response createSystemImplementation(String implJson) {
        try {
            SystemImplementationDto implDto = JsonUtil.fromJson(implJson, SystemImplementationDto.class);
            SystemImplementationDto createdImpl = systemImplementationService.createSystemImplementation(implDto);
            return Response.status(Response.Status.CREATED).entity(JsonUtil.toJson(createdImpl)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @GET
    @Path("/implementations")
    @RolesAllowed({"security_officer", "system_owner", "admin"})
    public Response getAllSystemImplementations() {
        try {
            List<SystemImplementationDto> implementations = systemImplementationService.getAllSystemImplementations();
            return Response.ok(JsonUtil.toJson(implementations)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "Failed to retrieve system implementations")))
                    .build();
        }
    }

    @GET
    @Path("/implementations/{id}")
    @RolesAllowed({"security_officer", "system_owner", "admin"})
    public Response getSystemImplementationById(@PathParam("id") String idStr) {
        try {
            UUID id = UUID.fromString(idStr);
            SystemImplementationDto implementation = systemImplementationService.getSystemImplementationById(id);
            if (implementation != null) {
                return Response.ok(JsonUtil.toJson(implementation)).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(JsonUtil.toJson(Map.of("error", "System implementation not found")))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "Failed to retrieve system implementation")))
                    .build();
        }
    }

    @PUT
    @Path("/implementations/{id}")
    @RolesAllowed({"system_owner", "admin"})
    public Response updateSystemImplementation(@PathParam("id") String idStr, String implJson) {
        try {
            UUID id = UUID.fromString(idStr);
            SystemImplementationDto implDto = JsonUtil.fromJson(implJson, SystemImplementationDto.class);
            SystemImplementationDto updatedImpl = systemImplementationService.updateSystemImplementation(id, implDto);
            if (updatedImpl != null) {
                return Response.ok(JsonUtil.toJson(updatedImpl)).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @DELETE
    @Path("/implementations/{id}")
    @RolesAllowed({"system_owner", "admin"})
    public Response deleteSystemImplementation(@PathParam("id") String idStr) {
        try {
            UUID id = UUID.fromString(idStr);
            boolean deleted = systemImplementationService.deleteSystemImplementation(id);
            if (deleted) {
                return Response.ok(JsonUtil.toJson(Map.of("message", "System implementation deleted successfully"))).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(JsonUtil.toJson(Map.of("error", "System implementation not found")))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "System implementation deletion failed")))
                    .build();
        }
    }

    @GET
    @Path("/implementations/department/{departmentId}")
    @RolesAllowed({"security_officer", "system_owner", "admin"})
    public Response getSystemImplementationsByDepartment(@PathParam("departmentId") String departmentIdStr) {
        try {
            UUID departmentId = UUID.fromString(departmentIdStr);
            List<SystemImplementationDto> implementations = systemImplementationService.getImplementationsByDepartment(departmentId);
            return Response.ok(JsonUtil.toJson(implementations)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "Failed to retrieve system implementations by department")))
                    .build();
        }
    }

    @GET
    @Path("/{systemId}/implementations")
    @RolesAllowed({"security_officer", "system_owner", "admin"})
    public Response getSystemImplementationsBySystem(@PathParam("systemId") String systemIdStr) {
        try {
            UUID systemId = UUID.fromString(systemIdStr);
            List<SystemImplementationDto> implementations = systemImplementationService.getImplementationsBySystem(systemId);
            return Response.ok(JsonUtil.toJson(implementations)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "Failed to retrieve system implementations by system")))
                    .build();
        }
    }

    @POST
    @Path("/implementations/{id}/recalculate")
    public Response recalculateAndGetRiskScore(@PathParam("id") String idStr) {
        try {
            UUID id = UUID.fromString(idStr);
            SystemImplementationDto updatedImpl = systemImplementationService.recalculateRiskScore(id);
            if (updatedImpl != null) {
                return Response.ok(JsonUtil.toJson(updatedImpl)).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
}
