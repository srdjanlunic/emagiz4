package resource;

import model.Department;
import model.User;
import service.UserDepartmentService;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST resource for managing assignments between users and departments.
 */
@Path("/user-departments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserDepartmentResource {
    
    private final UserDepartmentService svc = new UserDepartmentService();
    
    /**
     * Assigns a user to a department.
     *
     * @param body JSON map containing keys "userId" and "departmentId" with UUID strings
     * @return HTTP 201 Created with confirmation message
     */
    @POST
    @RolesAllowed({"security_officer", "admin"})
    public Response assign(Map<String,String> body) {
        UUID userId = UUID.fromString(body.get("userId"));
        UUID deptId = UUID.fromString(body.get("departmentId"));
        svc.assignUserToDepartment(userId, deptId);
        return Response.status(Response.Status.CREATED)
                .entity(Map.of("message","User assigned to department"))
                .build();
    }
    
    /**
     * Removes a user from a department.
     *
     * @param u User UUID string path parameter
     * @param d Department UUID string path parameter
     * @return HTTP 200 OK with confirmation message if successful, 404 Not Found if assignment does not exist
     */
    @DELETE
    @Path("/{userId}/{departmentId}")
    @RolesAllowed({"security_officer", "admin"})
    public Response remove(@PathParam("userId") String u, @PathParam("departmentId") String d) {
        UUID userId = UUID.fromString(u);
        UUID deptId = UUID.fromString(d);
        boolean ok = svc.removeUserFromDepartment(userId, deptId);
        if (!ok) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error","Assignment not found"))
                    .build();
        }
        return Response.ok(Map.of("message","User unassigned from department")).build();
    }
    
    /**
     * Lists all departments that a user belongs to.
     *
     * @param u User UUID string path parameter
     * @return HTTP 200 OK with list of departments JSON
     */
    @GET
    @Path("/user/{userId}")
    @RolesAllowed({"security_officer", "admin"})
    public Response forUser(@PathParam("userId") String u) {
        UUID userId = UUID.fromString(u);
        List<Department> list = svc.getDepartmentsForUser(userId);
        return Response.ok(list).build();
    }
    
    /**
     * Lists all active users in a given department.
     *
     * @param d Department UUID string path parameter
     * @return HTTP 200 OK with list of users JSON
     */
    @GET
    @Path("/department/{departmentId}")
    @RolesAllowed({"security_officer", "admin"})
    public Response forDepartment(@PathParam("departmentId") String d) {
        UUID deptId = UUID.fromString(d);
        List<User> list = svc.getUsersForDepartment(deptId);
        return Response.ok(list).build();
    }
}
