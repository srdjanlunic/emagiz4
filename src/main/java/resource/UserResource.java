package resource;

import service.UserService;
import model.User;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.annotation.security.RolesAllowed;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
    private UserService userService;

    public UserResource() {
        this.userService = new UserService();
    }

    // create user (admin only)
    @POST
    @RolesAllowed({"admin"})
    public Response createUser(User user) {
        // TODO: create new user
        // TODO: return created user or error
        return null;
    }

    // get all users
    @GET
    @RolesAllowed({"admin", "security_officer"})
    public Response getAllUsers() {
        // TODO: get all users
        // TODO: return user list
        return null;
    }

    // get user by id
    @GET
    @Path("/{id}")
    @RolesAllowed({"admin", "security_officer"})
    public Response getUserById(@PathParam("id") Long id) {
        // TODO: get user by id
        // TODO: return user or error
        return null;
    }

    // update user
    @PUT
    @Path("/{id}")
    @RolesAllowed({"admin"})
    public Response updateUser(@PathParam("id") Long id, User user) {
        // TODO: update user
        // TODO: return updated user or error
        return null;
    }

    // delete user
    @DELETE
    @Path("/{id}")
    @RolesAllowed({"admin"})
    public Response deleteUser(@PathParam("id") Long id) {
        // TODO: delete user
        // TODO: return success or error
        return null;
    }
}
