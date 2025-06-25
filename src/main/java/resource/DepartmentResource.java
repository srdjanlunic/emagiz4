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

/**
 * REST resource for managing departments.
 */
@Path("/departments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DepartmentResource {
    private DepartmentService departmentService;
    
    /**
     * Default constructor initializes DepartmentService.
     */
    public DepartmentResource() {
        this.departmentService = new DepartmentService();
    }
    
    /**
     * Creates a new department.
     *
     * @param departmentJson JSON representation of the department to create
     * @return HTTP response with created department JSON or error message
     */
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
    
    /**
     * Retrieves all departments.
     *
     * @return HTTP response with list of departments in JSON or error message
     */
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
    
    /**
     * Retrieves a department by its ID.
     *
     * @param idStr the UUID string of the department to retrieve
     * @return HTTP response with department JSON or error message
     */
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
    
    /**
     * Updates an existing department by ID.
     *
     * @param idStr         the UUID string of the department to update
     * @param departmentJson JSON representation of the updated department
     * @return HTTP response with updated department JSON or error message
     */
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
    
    /**
     * Deletes a department by ID.
     *
     * @param idStr the UUID string of the department to delete
     * @return HTTP response confirming deletion or error message
     */
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
