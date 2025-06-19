// resource/UserDepartmentResource.java
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

@Path("/user-departments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserDepartmentResource {

    private final UserDepartmentService svc = new UserDepartmentService();

    /**
     * Assign a user to a department.
     * Body: { "userId": "...", "departmentId": "..." }
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
     * Remove a user from a department.
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
     * List all departments a user belongs to.
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
     * List all active users in a given department.
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
