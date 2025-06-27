package resource;

import jakarta.annotation.security.RolesAllowed;
import model.ITSystem;
import model.SystemImplementation;
import service.SystemImplementationService;
import service.SystemService;
import util.JsonUtil;
import dto.SystemDto;
import dto.SystemImplementationDto;
import dto.SystemRegistrationDto;
import dao.SystemImplementationDAO;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST resource for managing IT systems and their implementations.
 */
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
    
    /**
     * Creates a new system and its implementation.
     *
     * @param registrationDto JSON representation of SystemRegistrationDto
     * @return HTTP 201 Created with the created system implementation JSON or error response
     */
    @POST
    @RolesAllowed({"system_owner", "admin"})
    public Response createSystem(String systemJson) {
        logger.info("Received request to create system with JSON: {}", systemJson);
        try {
            SystemRegistrationDto registrationDto = JsonUtil.fromJson(systemJson, SystemRegistrationDto.class);
            logger.info("Successfully parsed SystemRegistrationDto");
            SystemImplementation createdImplementation = systemService.registerSystem(registrationDto);
            logger.info("Successfully created system implementation: {}", createdImplementation.getId());
            return Response.status(Response.Status.CREATED).entity(JsonUtil.toJson(createdImplementation)).build();
        } catch (RuntimeException e) {
            logger.error("Error processing system registration: {}", e.getMessage());
            if (e.getCause() instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Invalid format for ownerId. It must be a valid UUID string.\"}")
                        .build();
            }
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Invalid request data: " + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            logger.error("Error creating system", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
    
    /**
     * Retrieves all systems.
     *
     * @return HTTP 200 OK with list of systems JSON or error response
     */
    @GET
    @RolesAllowed({"security_officer", "system_owner", "admin"})
    public Response getAllSystems(@QueryParam("page") @DefaultValue("1") int page,
                                  @QueryParam("pageSize") @DefaultValue("10") int pageSize) {
        try {
            Map<String, Object> responseData = systemService.getAllSystems(page, pageSize);
            return Response.ok(JsonUtil.toJson(responseData)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
    
    /**
     * Retrieves systems owned by a specific user.
     *
     * @param ownerIdStr UUID string of the owner (user) ID
     * @return HTTP 200 OK with list of systems JSON or error response
     */
    @GET
    @Path("/owner/{ownerId}")
    @RolesAllowed({"security_officer", "system_owner", "admin"})
    public Response getSystemsByOwner(@PathParam("ownerId") String ownerIdStr) {
        try {
            UUID ownerId = UUID.fromString(ownerIdStr);
            List<SystemDto> systems = systemService.getSystemsByOwner(ownerId);
            return Response.ok(JsonUtil.toJson(systems)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
    
    /**
     * Retrieves a system by its ID.
     *
     * @param idStr UUID string of the system ID
     * @return HTTP 200 OK with system JSON if found, 404 if not found, or error response
     */
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
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
    
    /**
     * Updates a system by its ID.
     *
     * @param idStr UUID string of the system ID
     * @param systemJson JSON representation of SystemDto with updated fields
     * @return HTTP 200 OK with updated system JSON if successful, 404 if not found, or error response
     */
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
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
    
    /**
     * Deletes a system by its ID.
     *
     * @param idStr UUID string of the system ID
     * @return HTTP 200 OK if deleted, 404 if not found, or error response
     */
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
    
    /**
     * Creates a new system implementation.
     *
     * @param implJson JSON representation of SystemImplementationDto
     * @return HTTP 201 Created with created implementation JSON or error response
     */
    @POST
    @Path("/implementations")
    @RolesAllowed({"system_owner", "admin"})
    public Response createSystemImplementation(String implJson) {
        try {
            SystemImplementationDto implDto = JsonUtil.fromJson(implJson, SystemImplementationDto.class);
            SystemImplementationDto createdImpl = systemImplementationService.createSystemImplementation(implDto);
            return Response.status(Response.Status.CREATED).entity(JsonUtil.toJson(createdImpl)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
    
    /**
     * Retrieves all system implementations.
     *
     * @return HTTP 200 OK with list of implementations JSON or error response
     */
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
    
    /**
     * Retrieves a system implementation by its ID.
     *
     * @param idStr UUID string of the system implementation ID
     * @return HTTP 200 OK with implementation JSON if found, 404 if not found, or error response
     */
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
    
    /**
     * Updates a system implementation by its ID.
     *
     * @param idStr UUID string of the system implementation ID
     * @param implJson JSON representation of updated SystemImplementationDto
     * @return HTTP 200 OK with updated implementation JSON if successful, 404 if not found, or error response
     */
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
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
    
    /**
     * Deletes a system implementation by its ID.
     *
     * @param idStr UUID string of the system implementation ID
     * @return HTTP 200 OK if deleted, 404 if not found, or error response
     */
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
    
    /**
     * Retrieves system implementations by department ID.
     *
     * @param departmentIdStr UUID string of the department ID
     * @return HTTP 200 OK with list of implementations JSON or error response
     */
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
    
    /**
     * Retrieves system implementations by system ID.
     *
     * @param systemIdStr UUID string of the system ID
     * @return HTTP 200 OK with list of implementations JSON or error response
     */
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
    
    /**
     * Recalculates and returns the risk score for a given system implementation ID.
     *
     * @param idStr UUID string of the system implementation ID
     * @return HTTP 200 OK with updated implementation JSON if successful, 404 if not found, or error response
     */
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
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
}
