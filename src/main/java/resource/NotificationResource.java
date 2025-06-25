package resource;

import jakarta.annotation.security.RolesAllowed;
import service.NotificationService;
import dto.NotificationDto;
import util.JsonUtil;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST resource for managing notifications.
 */
@Path("/notifications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NotificationResource {
    private final NotificationService notificationService;
    
    /**
     * Constructor initializes NotificationService.
     */
    public NotificationResource() {
        this.notificationService = new NotificationService();
    }
    
    /**
     * Retrieves all notifications for a given user.
     *
     * @param userIdStr The UUID string of the user.
     * @return JSON list of notifications or error response.
     */
    @GET
    @Path("/user/{userId}")
    @RolesAllowed({"system_owner", "admin"})
    public Response getNotificationsForUser(@PathParam("userId") String userIdStr) {
        try {
            UUID userId = UUID.fromString(userIdStr);
            List<NotificationDto> notifications = notificationService.getNotificationsForUser(userId);
            return Response.ok(JsonUtil.toJson(notifications)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "Failed to retrieve notifications: " + e.getMessage())))
                    .build();
        }
    }
    
    /**
     * Retrieves unread notifications for a given user.
     *
     * @param userIdStr The UUID string of the user.
     * @return JSON list of unread notifications or error response.
     */
    @GET
    @Path("/user/{userId}/unread")
    @RolesAllowed({"system_owner", "admin"})
    public Response getUnreadNotificationsForUser(@PathParam("userId") String userIdStr) {
        try {
            UUID userId = UUID.fromString(userIdStr);
            List<NotificationDto> notifications = notificationService.getUnreadNotificationsForUser(userId);
            return Response.ok(JsonUtil.toJson(notifications)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "Failed to retrieve unread notifications: " + e.getMessage())))
                    .build();
        }
    }
    
    /**
     * Marks a notification as read.
     *
     * @param notificationIdStr The UUID string of the notification.
     * @return HTTP 200 if successful, 404 if not found, or 500 on error.
     */
    @PUT
    @Path("/{notificationId}/read")
    @RolesAllowed({"system_owner", "admin"})
    public Response markNotificationAsRead(@PathParam("notificationId") String notificationIdStr) {
        try {
            UUID notificationId = UUID.fromString(notificationIdStr);
            boolean success = notificationService.markNotificationAsRead(notificationId);
            if (success) {
                return Response.ok().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "Failed to mark notification as read: " + e.getMessage())))
                    .build();
        }
    }
    
    /**
     * Creates a new notification.
     *
     * @param notificationJson JSON string containing notification creation data.
     * @return HTTP 201 with created notification JSON or error response.
     */
    @POST
    // TODO: Add role-based access control
    public Response createNotification(String notificationJson) {
        try {
            CreateNotificationRequest request = JsonUtil.fromJson(notificationJson, CreateNotificationRequest.class);
            NotificationDto notification = notificationService.createNotification(
                    UUID.fromString(request.userId),
                    request.type,
                    request.message,
                    request.vulnerabilityId != null ? UUID.fromString(request.vulnerabilityId) : null
            );
            
            if (notification != null) {
                return Response.status(Response.Status.CREATED)
                        .entity(JsonUtil.toJson(notification))
                        .build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(JsonUtil.toJson(Map.of("error", "Failed to create notification")))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(JsonUtil.toJson(Map.of("error", "Notification creation failed: " + e.getMessage())))
                    .build();
        }
    }
    
    /**
     * DTO for creating notification requests.
     */
    public static class CreateNotificationRequest {
        /** User ID to receive the notification */
        public String userId;
        /** Notification message content */
        public String message;
        /** Notification type */
        public String type;
        /** Optional vulnerability ID related to the notification */
        public String vulnerabilityId;
    }
}
