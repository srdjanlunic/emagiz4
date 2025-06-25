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

/**
 * Service for managing notifications related to users and vulnerabilities.
 */
public class NotificationService {
    private NotificationDAO notificationDAO;
    private SystemImplementationDAO systemImplementationDAO;
    private UserDAO userDAO;
    private VulnerabilityDAO vulnerabilityDAO;
    
    /**
     * Constructs a NotificationService with DAO dependencies.
     */
    public NotificationService() {
        this.notificationDAO = new NotificationDAO();
        this.systemImplementationDAO = new SystemImplementationDAO();
        this.userDAO = new UserDAO();
        this.vulnerabilityDAO = new VulnerabilityDAO();
    }
    
    /**
     * Creates a new notification for a user.
     *
     * @param userId   the ID of the user to notify
     * @param type     the type of notification (e.g., info, warning)
     * @param message  the message content of the notification
     * @param entityId optional related entity ID (e.g., vulnerability ID), may be null
     * @return the created NotificationDto
     */
    public NotificationDto createNotification(UUID userId, String type, String message, UUID entityId) {
        Notification notification = new Notification();
        notification.setId(UUID.randomUUID());
        notification.setUserId(userId);
        notification.setMessage(message);
        notification.setType(type);
        // notification.setEntityId(entityId); // If linking to entity supported
        Notification createdNotification = notificationDAO.create(notification);
        return toDto(createdNotification);
    }
    
    /**
     * Retrieves all notifications for a given user.
     *
     * @param userId the user ID to retrieve notifications for
     * @return list of NotificationDto objects for the user
     */
    public List<NotificationDto> getNotificationsForUser(UUID userId) {
        return notificationDAO.findByUser(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Retrieves all unread notifications for a given user.
     *
     * @param userId the user ID to retrieve unread notifications for
     * @return list of unread NotificationDto objects for the user
     */
    public List<NotificationDto> getUnreadNotificationsForUser(UUID userId) {
        return notificationDAO.findUnreadByUser(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Marks a notification as read.
     *
     * @param notificationId the notification ID to mark as read
     * @return true if the notification was successfully marked as read, false otherwise
     */
    public boolean markNotificationAsRead(UUID notificationId) {
        return notificationDAO.markAsRead(notificationId);
    }
    
    /**
     * Creates notifications for system owners related to a vulnerability match.
     * Placeholder method for future implementation.
     *
     * @param vulnerabilityId          the vulnerability ID involved
     * @param cveId                   the CVE identifier
     * @param systemImplementationIds list of system implementation IDs affected
     */
    public void createVulnerabilityMatchNotifications(UUID vulnerabilityId, String cveId, List<UUID> systemImplementationIds) {
        System.out.println("Placeholder for creating vulnerability match notifications.");
    }
    
    /**
     * Sends a notification.
     * Currently only logs the notification, can be extended to send emails or push notifications.
     *
     * @param notification the notification to send
     */
    public void sendNotification(Notification notification) {
        System.out.println("Notification sent to user " + notification.getUserId() + ": " + notification.getMessage());
    }
    
    /**
     * Converts a Notification entity to a NotificationDto.
     *
     * @param notification the Notification entity
     * @return the corresponding NotificationDto
     */
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
