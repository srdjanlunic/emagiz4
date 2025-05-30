package service;

import dao.NotificationDAO;
import model.Notification;
import java.util.List;

public class NotificationService {
    private NotificationDAO notificationDAO;

    public NotificationService() {
        this.notificationDAO = new NotificationDAO();
    }

    // create notification for user
    public Notification createNotification(Long userId, String message, String type) {
        // TODO: create notification object
        // TODO: save to database
        return null;
    }

    // get notifications for user
    public List<Notification> getNotificationsForUser(Long userId) {
        // TODO: get notifications from database
        return null;
    }

    // get unread notifications for user
    public List<Notification> getUnreadNotificationsForUser(Long userId) {
        // TODO: get unread notifications from database
        return null;
    }

    // mark notification as read
    public boolean markNotificationAsRead(Long notificationId) {
        // TODO: update notification in database
        return false;
    }

    // create vulnerability notification for system owner
    public void createVulnerabilityNotification(Long systemId, Long vulnerabilityId, String cveId) {
        // TODO: find system owner
        // TODO: create notification message
        // TODO: save notification
    }

    // send notification (could be email, in-app, etc.)
    public void sendNotification(Notification notification) {
        // TODO: implement notification sending logic
    }
}
