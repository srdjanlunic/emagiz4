package resource;

import jakarta.annotation.security.RolesAllowed;
import service.DepartmentService;
import model.Department;
import util.JsonUtil;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.Timestamp;
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
    }
    
    
    @POST
    @RolesAllowed({"security_officer", "admin"})
    public Response createDepartment(String departmentJson) {
        try {
            Department department = JsonUtil.fromJson(departmentJson, Department.class);

            if (department.getName() == null || department.getName().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(JsonUtil.toJson(Map.of("error", "Department name is required")))
                        .build();
            }

            /*
            if (department.getOrganizationId() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(JsonUtil.toJson(Map.of("error", "Organization ID is required")))
                        .build();
            }
            */

            if (department.getCreatedAt() == null) {
                department.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            }

            Department createdDepartment = departmentService.createDepartment(department);

            if (createdDepartment != null) {
                return Response.status(Response.Status.CREATED)
                        .entity(JsonUtil.toJson(createdDepartment))
                        .build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(JsonUtil.toJson(Map.of("error", "Failed to create department")))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "Department creation failed")))
                    .build();
        }
    }

    @GET
    @RolesAllowed({"security_officer", "admin"})
    public Response getAllDepartments() {
        try {
            List<Department> departments = departmentService.getAllDepartments();
            return Response.ok(JsonUtil.toJson(departments)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "Failed to retrieve departments")))
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"security_officer", "admin"})
    public Response getDepartmentById(@PathParam("id") String idStr) {
        try {
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
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "Failed to retrieve department")))
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"security_officer", "admin"})
    public Response updateDepartment(@PathParam("id") String idStr, String departmentJson) {
        try {
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
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "Department update failed")))
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"security_officer", "admin"})
    public Response deleteDepartment(@PathParam("id") String idStr) {
        try {
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
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "Department deletion failed")))
                    .build();
        }
    }
}
