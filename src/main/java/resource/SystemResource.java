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

    // ========== ITSystem endpoints ==========

    @POST
    public Response createSystem(String systemJson) {
        System.out.println("=== SystemResource.createSystem - ENTRY POINT ===");
        System.out.println("Method called at: " + new java.util.Date());
        System.out.println("Thread: " + Thread.currentThread().getName());

        try {
            System.out.println("=== SystemResource.createSystem called ===");
            System.out.println("Received JSON: " + systemJson);
            System.out.println("JSON length: " + (systemJson != null ? systemJson.length() : "null"));

            // Validate input
            if (systemJson == null || systemJson.trim().isEmpty()) {
                System.out.println("ERROR: Empty or null JSON received");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(JsonUtil.toJson(Map.of("error", "Request body is empty")))
                        .build();
            }

            // Parse JSON to ITSystem object
            ITSystem system;
            try {
                System.out.println("About to parse JSON with JsonUtil.fromJson...");
                system = JsonUtil.fromJson(systemJson, ITSystem.class);
                System.out.println("JSON parsed successfully");
                System.out.println("System name: " + system.getName());
                System.out.println("System vendor: " + system.getVendor());
                System.out.println("System description: " + system.getDescription());
            } catch (Exception jsonException) {
                System.out.println("ERROR: JSON parsing failed: " + jsonException.getMessage());
                jsonException.printStackTrace();
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(JsonUtil.toJson(Map.of("error", "Invalid JSON format: " + jsonException.getMessage())))
                        .build();
            }

            // Validate required fields
            if (system.getName() == null || system.getName().trim().isEmpty()) {
                System.out.println("ERROR: System name is required");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(JsonUtil.toJson(Map.of("error", "System name is required")))
                        .build();
            }

            // Set timestamp if not provided
            if (system.getCreatedAt() == null) {
                system.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                System.out.println("Set createdAt timestamp: " + system.getCreatedAt());
            }

            System.out.println("About to call systemService.createSystem...");
            ITSystem createdSystem = systemService.createSystem(system);
            System.out.println("systemService.createSystem returned: " + (createdSystem != null ? "SUCCESS" : "NULL"));

            if (createdSystem != null) {
                System.out.println("✅ System created successfully with ID: " + createdSystem.getId());
                String responseJson = JsonUtil.toJson(createdSystem);
                System.out.println("Response JSON: " + responseJson);
                return Response.status(Response.Status.CREATED)
                        .entity(responseJson)
                        .build();
            } else {
                System.out.println("❌ SystemService.createSystem returned null");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(JsonUtil.toJson(Map.of("error", "Failed to create system - database operation failed")))
                        .build();
            }
        } catch (Exception e) {
            System.out.println("❌ Exception in SystemResource.createSystem: " + e.getMessage());
            e.printStackTrace();
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

    // ========== SystemImplementation endpoints ==========

    @POST
    @Path("/implementations")
    public Response createSystemImplementation(String implementationJson) {
        try {
            System.out.println("=== SystemResource.createSystemImplementation - ENTRY POINT ===");
            System.out.println("Method called at: " + new java.util.Date());
            System.out.println("Thread: " + Thread.currentThread().getName());
            System.out.println("Received JSON: " + implementationJson);
            System.out.println("JSON length: " + (implementationJson != null ? implementationJson.length() : "null"));

            // Parse JSON to SystemImplementation object
            SystemImplementation implementation;
            try {
                System.out.println("About to parse JSON with JsonUtil.fromJson...");
                implementation = JsonUtil.fromJson(implementationJson, SystemImplementation.class);
                System.out.println("JSON parsed successfully");
                System.out.println("SystemID: " + implementation.getSystemId());
                System.out.println("DepartmentID: " + implementation.getDepartmentId());
                System.out.println("CriticalityLevel: " + implementation.getCriticalityLevel());
            } catch (Exception jsonException) {
                System.out.println("ERROR: JSON parsing failed: " + jsonException.getMessage());
                jsonException.printStackTrace();
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(JsonUtil.toJson(Map.of("error", "Invalid JSON format: " + jsonException.getMessage())))
                        .build();
            }

            // Validate required fields
            if (implementation.getSystemId() == null) {
                System.out.println("ERROR: System ID is required");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(JsonUtil.toJson(Map.of("error", "System ID is required")))
                        .build();
            }

            if (implementation.getDepartmentId() == null) {
                System.out.println("ERROR: Department ID is required");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(JsonUtil.toJson(Map.of("error", "Department ID is required")))
                        .build();
            }

            // Check if criticality level is valid
            String criticalityLevel = implementation.getCriticalityLevel();
            if (criticalityLevel != null && !criticalityLevel.equals("HIGH") &&
                    !criticalityLevel.equals("MEDIUM") && !criticalityLevel.equals("LOW")) {
                System.out.println("ERROR: Invalid criticality level: " + criticalityLevel);
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(JsonUtil.toJson(Map.of("error", "Invalid criticality level. Must be HIGH, MEDIUM, or LOW")))
                        .build();
            }

            System.out.println("About to call systemImplementationService.createSystemImplementation...");
            SystemImplementation createdImplementation = systemImplementationService.createSystemImplementation(implementation);
            System.out.println("systemImplementationService.createSystemImplementation returned: " +
                    (createdImplementation != null ? "SUCCESS" : "NULL"));

            if (createdImplementation != null) {
                System.out.println("✅ System implementation created successfully with ID: " + createdImplementation.getId());
                String responseJson = JsonUtil.toJson(createdImplementation);
                System.out.println("Response JSON: " + responseJson);
                return Response.status(Response.Status.CREATED)
                        .entity(responseJson)
                        .build();
            } else {
                System.out.println("❌ SystemImplementationService.createSystemImplementation returned null");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(JsonUtil.toJson(Map.of("error", "Failed to create system implementation")))
                        .build();
            }
        } catch (Exception e) {
            System.out.println("❌ Exception in SystemResource.createSystemImplementation: " + e.getMessage());
            e.printStackTrace();
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
