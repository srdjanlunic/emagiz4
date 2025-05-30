package resource;

import service.AuthService;
import model.User;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {
    private AuthService authService;

    public AuthResource() {
        this.authService = new AuthService();
    }

    // login endpoint
    @POST
    @Path("/login")
    public Response login(LoginRequest loginRequest) {
        // TODO: validate login credentials
        // TODO: generate JWT token
        // TODO: return token or error
        return null;
    }

    // logout endpoint
    @POST
    @Path("/logout")
    public Response logout() {
        // TODO: implement logout logic
        return null;
    }

    // validate token endpoint
    @GET
    @Path("/validate")
    public Response validateToken(@HeaderParam("Authorization") String authHeader) {
        // TODO: validate JWT token
        // TODO: return user info or error
        return null;
    }

    // inner class for login request
    public static class LoginRequest {
        public String username;
        public String password;
    }
}
