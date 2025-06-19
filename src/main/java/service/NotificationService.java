package service;

import dao.NotificationDAO;
import dao.SystemImplementationDAO;
import dao.UserDAO;
import dao.VulnerabilityDAO;
import model.Notification;
import model.SystemImplementation;
import model.User;
import model.Vulnerability;
import model.VulnerabilityMatch;
import dto.NotificationDto;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class NotificationService {
    private NotificationDAO notificationDAO;
    private SystemImplementationDAO systemImplementationDAO;
    private UserDAO userDAO;
    private VulnerabilityDAO vulnerabilityDAO;

    public NotificationService() {
        this.notificationDAO = new NotificationDAO();
        this.systemImplementationDAO = new SystemImplementationDAO();
        this.userDAO = new UserDAO();
        this.vulnerabilityDAO = new VulnerabilityDAO();
    }

    // create notification for user
    public NotificationDto createNotification(UUID userId, String type, String message, UUID entityId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessage(message);
        notification.setType(type);
        // In a real app, you might want to link notifications to specific entities
        // notification.setEntityId(entityId);
        Notification createdNotification = notificationDAO.create(notification);
        return toDto(createdNotification);
    }

    // get notifications for user
    public List<NotificationDto> getNotificationsForUser(UUID userId) {
        return notificationDAO.findByUser(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // get unread notifications for user
    public List<NotificationDto> getUnreadNotificationsForUser(UUID userId) {
        return notificationDAO.findUnreadByUser(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // mark notification as read
    public boolean markNotificationAsRead(UUID notificationId) {
        return notificationDAO.markAsRead(notificationId);
    }

    // create vulnerability match notification for system owners
    public void createVulnerabilityMatchNotifications(UUID vulnerabilityId, String cveId, List<UUID> systemImplementationIds) {
        // In a more complex system, we would look up system owners and create targeted notifications.
        // For now, we'll keep it simple and assume a way to get relevant users.
        // This part needs to be connected with User and Department services.
        System.out.println("Placeholder for creating vulnerability match notifications.");
    }

    // send notification (could be email, in-app, etc.)
    public void sendNotification(Notification notification) {
        // For now, just store in database (in-app notification)
        // In the future, this could send emails, push notifications, etc.
        System.out.println("Notification sent to user " + notification.getUserId() + ": " + notification.getMessage());
    }

    private NotificationDto toDto(Notification notification) {
        return new NotificationDto(
                notification.getId(),
                notification.getUserId(),
                notification.getMessage(),
                notification.getType(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}
