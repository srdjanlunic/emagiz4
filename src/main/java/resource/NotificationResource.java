package resource;

import service.NotificationService;
import model.Notification;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.annotation.security.RolesAllowed;

@Path("/notifications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NotificationResource {
    private NotificationService notificationService;

    public NotificationResource() {
        this.notificationService = new NotificationService();
    }

    // get notifications for current user
    @GET
    @RolesAllowed({"system_owner", "security_officer", "technical_expert"})
    public Response getNotifications() {
        // TODO: get current user from JWT
        // TODO: get notifications for user
        // TODO: return notification list
        return null;
    }

    // get unread notifications for current user
    @GET
    @Path("/unread")
    @RolesAllowed({"system_owner", "security_officer", "technical_expert"})
    public Response getUnreadNotifications() {
        // TODO: get current user from JWT
        // TODO: get unread notifications for user
        // TODO: return notification list
        return null;
    }

    // mark notification as read
    @PUT
    @Path("/{id}/read")
    @RolesAllowed({"system_owner", "security_officer", "technical_expert"})
    public Response markNotificationAsRead(@PathParam("id") Long id) {
        // TODO: mark notification as read
        // TODO: return success or error
        return null;
    }
}
