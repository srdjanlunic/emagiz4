package resource;

import service.DepartmentService;
import model.Department;
import util.JsonUtil;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Path("/departments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DepartmentResource {
    private DepartmentService departmentService;

    public DepartmentResource() {
        this.departmentService = new DepartmentService();
        System.out.println("=== DepartmentResource initialized ===");
    }

    @POST
    public Response createDepartment(String departmentJson) {
        try {
            System.out.println("=== Creating Department ===");
            System.out.println("Received JSON: " + departmentJson);

            Department department = JsonUtil.fromJson(departmentJson, Department.class);
            Department createdDepartment = departmentService.createDepartment(department);

            return Response.status(Response.Status.CREATED)
                    .entity(JsonUtil.toJson(createdDepartment))
                    .build();

        } catch (IllegalArgumentException e) {
            System.out.println("Validation error: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(JsonUtil.toJson(Map.of("error", e.getMessage())))
                    .build();
        } catch (Exception e) {
            System.out.println("Exception in createDepartment: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "Department creation failed: " + e.getMessage())))
                    .build();
        }
    }

    @GET
    public Response getAllDepartments() {
        try {
            System.out.println("=== Getting All Departments ===");
            List<Department> departments = departmentService.getAllDepartments();
            System.out.println("Found " + departments.size() + " departments");
            return Response.ok(JsonUtil.toJson(departments)).build();
        } catch (Exception e) {
            System.out.println("Exception in getAllDepartments: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "Failed to retrieve departments: " + e.getMessage())))
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getDepartmentById(@PathParam("id") String idStr) {
        try {
            System.out.println("=== Getting Department by ID: " + idStr + " ===");
            UUID id = UUID.fromString(idStr);
            Department department = departmentService.getDepartmentById(id);

            if (department != null) {
                return Response.ok(JsonUtil.toJson(department)).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(JsonUtil.toJson(Map.of("error", "Department not found")))
                        .build();
            }
        } catch (Exception e) {
            System.out.println("Exception in getDepartmentById: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "Failed to retrieve department: " + e.getMessage())))
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateDepartment(@PathParam("id") String idStr, String departmentJson) {
        try {
            System.out.println("=== Updating Department: " + idStr + " ===");
            UUID id = UUID.fromString(idStr);
            Department department = JsonUtil.fromJson(departmentJson, Department.class);
            department.setId(id);

            Department updatedDepartment = departmentService.updateDepartment(department);

            if (updatedDepartment != null) {
                return Response.ok(JsonUtil.toJson(updatedDepartment)).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(JsonUtil.toJson(Map.of("error", "Department not found")))
                        .build();
            }
        } catch (Exception e) {
            System.out.println("Exception in updateDepartment: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "Department update failed: " + e.getMessage())))
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteDepartment(@PathParam("id") String idStr) {
        try {
            System.out.println("=== Deleting Department: " + idStr + " ===");
            UUID id = UUID.fromString(idStr);
            boolean deleted = departmentService.deleteDepartment(id);

            if (deleted) {
                return Response.ok(JsonUtil.toJson(Map.of("message", "Department deleted successfully"))).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(JsonUtil.toJson(Map.of("error", "Department not found")))
                        .build();
            }
        } catch (Exception e) {
            System.out.println("Exception in deleteDepartment: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "Department deletion failed: " + e.getMessage())))
                    .build();
        }
    }

    @GET
    @Path("/organization/{organizationId}")
    public Response getDepartmentsByOrganization(@PathParam("organizationId") String organizationIdStr) {
        try {
            System.out.println("=== Getting Departments by Organization: " + organizationIdStr + " ===");
            UUID organizationId = UUID.fromString(organizationIdStr);
            List<Department> departments = departmentService.getDepartmentsByOrganization(organizationId);
            return Response.ok(JsonUtil.toJson(departments)).build();
        } catch (Exception e) {
            System.out.println("Exception in getDepartmentsByOrganization: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "Failed to retrieve departments by organization: " + e.getMessage())))
                    .build();
        }
    }
}
