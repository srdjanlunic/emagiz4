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
import java.util.List;
import java.util.UUID;

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
    public Notification createNotification(UUID userId, String message, String type) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);

        return notificationDAO.create(notification);
    }

    // get notifications for user
    public List<Notification> getNotificationsForUser(UUID userId) {
        return notificationDAO.findByUser(userId);
    }

    // get unread notifications for user
    public List<Notification> getUnreadNotificationsForUser(UUID userId) {
        return notificationDAO.findUnreadByUser(userId);
    }

    // mark notification as read
    public boolean markNotificationAsRead(UUID notificationId) {
        return notificationDAO.markAsRead(notificationId);
    }

    // create vulnerability match notification for system owners
    public void createVulnerabilityMatchNotification(VulnerabilityMatch match) {
        SystemImplementation implementation = systemImplementationDAO.findById(match.getSystemImplementationId());
        if (implementation == null) {
            return;
        }

        Vulnerability vulnerability = vulnerabilityDAO.findById(match.getVulnerabilityId());
        if (vulnerability == null) {
            return;
        }

        // Find system owners for this implementation
        List<User> systemOwners = userDAO.findByDepartment(implementation.getDepartmentId());

        for (User owner : systemOwners) {
            // Create notification message
            String message = String.format(
                    "New vulnerability %s detected for system implementation in your department. Please review and assess the impact.",
                    vulnerability.getCveId()
            );

            // Create notification
            Notification notification = new Notification();
            notification.setUserId(owner.getId());
            notification.setMatchId(match.getId());
            notification.setSystemId(implementation.getId());
            notification.setVulnerabilityId(vulnerability.getId());
            notification.setMessage(message);
            notification.setType("VULNERABILITY_MATCH");
            notification.setPriority("HIGH");
            notification.setRead(false);

            notificationDAO.create(notification);
        }
    }

    // send notification (could be email, in-app, etc.)
    public void sendNotification(Notification notification) {
        // For now, just store in database (in-app notification)
        // In the future, this could send emails, push notifications, etc.
        System.out.println("Notification sent to user " + notification.getUserId() + ": " + notification.getMessage());
    }
}
