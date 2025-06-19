package resource;

import jakarta.annotation.security.RolesAllowed;
import service.UserService;
import model.User;
import util.JsonUtil;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import dto.UserDto;
import dto.UserCreationRequestDto;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
    private UserService userService;

    public UserResource() {
        this.userService = new UserService();
    }

    @POST
    public Response createUser(String userJson) {
        try {
            UserCreationRequestDto request = JsonUtil.fromJson(userJson, UserCreationRequestDto.class);
            User createdUser = userService.createUser(request);

            if (createdUser != null) {
                return Response.status(Response.Status.CREATED)
                        .entity(JsonUtil.toJson(createdUser))
                        .build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(JsonUtil.toJson(Map.of("error", "Failed to create user")))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "User creation failed: " + e.getMessage())))
                    .build();
        }
    }

    @GET
    @RolesAllowed({"security_officer", "admin"})
    public Response getAllUsers() {
        try {
            List<UserDto> users = userService.getAllUsers();
            return Response.ok(JsonUtil.toJson(users)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "Failed to retrieve users: " + e.getMessage())))
                    .build();
        }
    }

    // get user by id
    @GET
    @Path("/{id}")
    @RolesAllowed({"security_officer", "admin"})
    public Response getUserById(@PathParam("id") String idStr) {
        try {
            UUID id = UUID.fromString(idStr);
            User user = userService.getUserById(id);
            if (user != null) {
                return Response.ok(JsonUtil.toJson(user)).header("Cache-Control", "no-store").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(JsonUtil.toJson(Map.of("error", "User not found")))
                        .header("Cache-Control", "no-store")
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "Failed to retrieve user: " + e.getMessage())))
                    .header("Cache-Control", "no-store")
                    .build();
        }
    }

    // update user
    @PUT
    @Path("/{id}")
    @RolesAllowed({"security_officer", "admin"})
    public Response updateUser(@PathParam("id") String idStr, String userJson) {
        try {
            UUID id = UUID.fromString(idStr);
            User user = JsonUtil.fromJson(userJson, User.class);
            user.setId(id);
            User updatedUser = userService.updateUser(user);

            if (updatedUser != null) {
                return Response.ok(JsonUtil.toJson(updatedUser)).header("Cache-Control", "no-store").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(JsonUtil.toJson(Map.of("error", "User not found")))
                        .header("Cache-Control", "no-store")
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "User update failed: " + e.getMessage())))
                    .header("Cache-Control", "no-store")
                    .build();
        }
    }

    // delete user
    @DELETE
    @Path("/{id}")
    @RolesAllowed({"security_officer", "admin"})
    public Response deleteUser(@PathParam("id") String idStr) {
        try {
            UUID id = UUID.fromString(idStr);
            boolean deleted = userService.deleteUser(id);
            if (deleted) {
                return Response.ok(JsonUtil.toJson(Map.of("message", "User deleted successfully"))).header("Cache-Control", "no-store").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(JsonUtil.toJson(Map.of("error", "User not found")))
                        .header("Cache-Control", "no-store")
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "User deletion failed: " + e.getMessage())))
                    .header("Cache-Control", "no-store")
                    .build();
        }
    }

    // get users by department
    @GET
    @Path("/department/{departmentId}")
    @RolesAllowed({"security_officer", "admin"})
    public Response getUsersByDepartment(@PathParam("departmentId") String departmentIdStr) {
        try {
            UUID departmentId = UUID.fromString(departmentIdStr);
            List<User> users = userService.getUsersByDepartment(departmentId);
            return Response.ok(JsonUtil.toJson(users)).header("Cache-Control", "no-store").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "Failed to retrieve users by department: " + e.getMessage())))
                    .header("Cache-Control", "no-store")
                    .build();
        }
    }
}
