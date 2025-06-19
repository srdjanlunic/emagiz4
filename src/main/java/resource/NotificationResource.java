package resource;

import service.NotificationService;
import dto.NotificationDto;
import util.JsonUtil;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Path("/notifications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NotificationResource {
    private final NotificationService notificationService;

    public NotificationResource() {
        this.notificationService = new NotificationService();
    }

    @GET
    @Path("/user/{userId}")
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

    @GET
    @Path("/user/{userId}/unread")
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

    @PUT
    @Path("/{notificationId}/read")
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

    @POST
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

    public static class CreateNotificationRequest {
        public String userId;
        public String message;
        public String type;
        public String vulnerabilityId;
    }
}
