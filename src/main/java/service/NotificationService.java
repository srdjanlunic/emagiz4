package service;

import dao.NotificationDAO;
import dao.SystemDAO;
import dao.UserDAO;
import model.Notification;
import model.ITSystem;
import model.User;
import java.util.List;

public class NotificationService {
    private NotificationDAO notificationDAO;
    private SystemDAO systemDAO;
    private UserDAO userDAO;

    public NotificationService() {
        this.notificationDAO = new NotificationDAO();
        this.systemDAO = new SystemDAO();
        this.userDAO = new UserDAO();
    }

    // create notification for user
    public Notification createNotification(Long userId, String message, String type) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);

        return notificationDAO.create(notification);
    }

    // get notifications for user
    public List<Notification> getNotificationsForUser(Long userId) {
        return notificationDAO.findByUser(userId);
    }

    // get unread notifications for user
    public List<Notification> getUnreadNotificationsForUser(Long userId) {
        return notificationDAO.findUnreadByUser(userId);
    }

    // mark notification as read
    public boolean markNotificationAsRead(Long notificationId) {
        return notificationDAO.markAsRead(notificationId);
    }

    // create vulnerability notification for system owner
    public void createVulnerabilityNotification(Long systemId, Long vulnerabilityId, String cveId) {
        ITSystem system = systemDAO.findById(systemId);
        if (system == null) {
            return;
        }

        // Find system owner
        User systemOwner = userDAO.findById(system.getOwnerId());
        if (systemOwner == null) {
            return;
        }

        // Create notification message
        String message = String.format(
                "New vulnerability %s detected for system '%s'. Please review and assess the impact.",
                cveId, system.getName()
        );

        // Create notification
        Notification notification = new Notification();
        notification.setUserId(systemOwner.getId());
        notification.setSystemId(systemId);
        notification.setVulnerabilityId(vulnerabilityId);
        notification.setMessage(message);
        notification.setType("VULNERABILITY_MATCH");
        notification.setRead(false);

        notificationDAO.create(notification);
    }

    // send notification (could be email, in-app, etc.)
    public void sendNotification(Notification notification) {
        // For now, just store in database (in-app notification)
        // In the future, this could send emails, push notifications, etc.
        System.out.println("Notification sent to user " + notification.getUserId() + ": " + notification.getMessage());
    }
}
