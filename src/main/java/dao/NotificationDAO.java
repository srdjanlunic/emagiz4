package dao;

import model.Notification;
import java.util.List;

public class NotificationDAO {

    // create new notification
    public Notification create(Notification notification) {
        // TODO: implement database insert
        return null;
    }

    // get notification by id
    public Notification findById(Long id) {
        // TODO: implement database query
        return null;
    }

    // find notifications by user
    public List<Notification> findByUser(Long userId) {
        // TODO: implement database query
        return null;
    }

    // find unread notifications by user
    public List<Notification> findUnreadByUser(Long userId) {
        // TODO: implement database query
        return null;
    }

    // mark notification as read
    public boolean markAsRead(Long id) {
        // TODO: implement database update
        return false;
    }

    // delete notification
    public boolean delete(Long id) {
        // TODO: implement database delete
        return false;
    }
}
