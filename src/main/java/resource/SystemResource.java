package resource;

import service.SystemService;
import service.SystemImplementationService;
import model.ITSystem;
import model.SystemImplementation;
import util.JsonUtil;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Path("/systems")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SystemResource {
    private SystemService systemService;
    private SystemImplementationService systemImplementationService;

    public SystemResource() {
        this.systemService = new SystemService();
        this.systemImplementationService = new SystemImplementationService();
    }

    @POST
    public Response createSystem(String systemJson) {
        try {
            if (systemJson == null || systemJson.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(JsonUtil.toJson(Map.of("error", "Request body is empty")))
                        .build();
            }

            ITSystem system = JsonUtil.fromJson(systemJson, ITSystem.class);

            if (system.getName() == null || system.getName().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(JsonUtil.toJson(Map.of("error", "System name is required")))
                        .build();
            }

            if (system.getCreatedAt() == null) {
                system.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            }

            ITSystem createdSystem = systemService.createSystem(system);

            if (createdSystem != null) {
                return Response.status(Response.Status.CREATED)
                        .entity(JsonUtil.toJson(createdSystem))
                        .build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(JsonUtil.toJson(Map.of("error", "Failed to create system")))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "System creation failed: " + e.getMessage())))
                    .build();
        }
    }

    @GET
    public Response getAllSystems() {
        try {
            List<ITSystem> systems = systemService.getAllSystems();
            return Response.ok(JsonUtil.toJson(systems)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "Failed to retrieve systems: " + e.getMessage())))
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getSystemById(@PathParam("id") String idStr) {
        try {
            UUID id = UUID.fromString(idStr);
            ITSystem system = systemService.getSystemById(id);
            if (system != null) {
                return Response.ok(JsonUtil.toJson(system)).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(JsonUtil.toJson(Map.of("error", "System not found")))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "Failed to retrieve system: " + e.getMessage())))
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateSystem(@PathParam("id") String idStr, String systemJson) {
        try {
            UUID id = UUID.fromString(idStr);
            ITSystem system = JsonUtil.fromJson(systemJson, ITSystem.class);
            system.setId(id);
            ITSystem updatedSystem = systemService.updateSystem(system);

            if (updatedSystem != null) {
                return Response.ok(JsonUtil.toJson(updatedSystem)).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(JsonUtil.toJson(Map.of("error", "System not found")))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "System update failed: " + e.getMessage())))
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
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
                    .entity(JsonUtil.toJson(Map.of("error", "System deletion failed: " + e.getMessage())))
                    .build();
        }
    }

    @POST
    @Path("/implementations")
    public Response createSystemImplementation(String implementationJson) {
        try {
            SystemImplementation implementation = JsonUtil.fromJson(implementationJson, SystemImplementation.class);

            if (implementation.getSystemId() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(JsonUtil.toJson(Map.of("error", "System ID is required")))
                        .build();
            }

            if (implementation.getDepartmentId() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(JsonUtil.toJson(Map.of("error", "Department ID is required")))
                        .build();
            }

            String criticalityLevel = implementation.getCriticalityLevel();
            if (criticalityLevel != null && !criticalityLevel.equals("HIGH") &&
                    !criticalityLevel.equals("MEDIUM") && !criticalityLevel.equals("LOW")) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(JsonUtil.toJson(Map.of("error", "Invalid criticality level. Must be HIGH, MEDIUM, or LOW")))
                        .build();
            }

            SystemImplementation createdImplementation = systemImplementationService.createSystemImplementation(implementation);

            if (createdImplementation != null) {
                return Response.status(Response.Status.CREATED)
                        .entity(JsonUtil.toJson(createdImplementation))
                        .build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(JsonUtil.toJson(Map.of("error", "Failed to create system implementation")))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "System implementation creation failed: " + e.getMessage())))
                    .build();
        }
    }

    @GET
    @Path("/implementations")
    public Response getAllSystemImplementations() {
        try {
            List<SystemImplementation> implementations = systemImplementationService.getAllSystemImplementations();
            return Response.ok(JsonUtil.toJson(implementations)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "Failed to retrieve system implementations: " + e.getMessage())))
                    .build();
        }
    }

    @GET
    @Path("/implementations/{id}")
    public Response getSystemImplementationById(@PathParam("id") String idStr) {
        try {
            UUID id = UUID.fromString(idStr);
            SystemImplementation implementation = systemImplementationService.getSystemImplementationById(id);
            if (implementation != null) {
                return Response.ok(JsonUtil.toJson(implementation)).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(JsonUtil.toJson(Map.of("error", "System implementation not found")))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "Failed to retrieve system implementation: " + e.getMessage())))
                    .build();
        }
    }

    @PUT
    @Path("/implementations/{id}")
    public Response updateSystemImplementation(@PathParam("id") String idStr, String implementationJson) {
        try {
            UUID id = UUID.fromString(idStr);
            SystemImplementation implementation = JsonUtil.fromJson(implementationJson, SystemImplementation.class);
            implementation.setId(id);
            SystemImplementation updatedImplementation = systemImplementationService.updateSystemImplementation(implementation);

            if (updatedImplementation != null) {
                return Response.ok(JsonUtil.toJson(updatedImplementation)).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(JsonUtil.toJson(Map.of("error", "System implementation not found")))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "System implementation update failed: " + e.getMessage())))
                    .build();
        }
    }

    @DELETE
    @Path("/implementations/{id}")
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
                    .entity(JsonUtil.toJson(Map.of("error", "System implementation deletion failed: " + e.getMessage())))
                    .build();
        }
    }

    @GET
    @Path("/implementations/department/{departmentId}")
    public Response getSystemImplementationsByDepartment(@PathParam("departmentId") String departmentIdStr) {
        try {
            UUID departmentId = UUID.fromString(departmentIdStr);
            List<SystemImplementation> implementations = systemImplementationService.getSystemImplementationsByDepartment(departmentId);
            return Response.ok(JsonUtil.toJson(implementations)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "Failed to retrieve system implementations by department: " + e.getMessage())))
                    .build();
        }
    }

    @GET
    @Path("/{systemId}/implementations")
    public Response getSystemImplementationsBySystem(@PathParam("systemId") String systemIdStr) {
        try {
            UUID systemId = UUID.fromString(systemIdStr);
            List<SystemImplementation> implementations = systemImplementationService.getSystemImplementationsBySystem(systemId);
            return Response.ok(JsonUtil.toJson(implementations)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "Failed to retrieve system implementations by system: " + e.getMessage())))
                    .build();
        }
    }
}
