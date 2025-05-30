package resource;

import service.NotificationService;
import model.Notification;
import util.JsonUtil;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.Map;

@Path("/notifications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NotificationResource {
    private NotificationService notificationService;

    public NotificationResource() {
        this.notificationService = new NotificationService();
    }

    // Get notifications for user
    @GET
    @Path("/user/{userId}")
    @RolesAllowed({"system_owner", "security_officer", "technical_expert"})
    public Response getNotificationsForUser(@PathParam("userId") Long userId) {
        try {
            List<Notification> notifications = notificationService.getNotificationsForUser(userId);
            return Response.ok(JsonUtil.toJson(notifications)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "Failed to retrieve notifications: " + e.getMessage())))
                    .build();
        }
    }

    // Get unread notifications for user
    @GET
    @Path("/user/{userId}/unread")
    @RolesAllowed({"system_owner", "security_officer", "technical_expert"})
    public Response getUnreadNotificationsForUser(@PathParam("userId") Long userId) {
        try {
            List<Notification> notifications = notificationService.getUnreadNotificationsForUser(userId);
            return Response.ok(JsonUtil.toJson(notifications)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "Failed to retrieve unread notifications: " + e.getMessage())))
                    .build();
        }
    }

    // Mark notification as read
    @PUT
    @Path("/{id}/read")
    @RolesAllowed({"system_owner", "security_officer", "technical_expert"})
    public Response markAsRead(@PathParam("id") Long id) {
        try {
            boolean success = notificationService.markNotificationAsRead(id);
            if (success) {
                return Response.ok(JsonUtil.toJson(Map.of("message", "Notification marked as read"))).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(JsonUtil.toJson(Map.of("error", "Notification not found")))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "Failed to mark notification as read: " + e.getMessage())))
                    .build();
        }
    }
}
