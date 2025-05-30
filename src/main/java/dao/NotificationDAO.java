package dao;

import config.DatabaseConfig;
import model.Notification;
import util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    // create new notification
    public Notification create(Notification notification) {
        String sql = "INSERT INTO notifications (message, user_id, system_id, vulnerability_id, is_read, type, created_at) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, notification.getMessage());
            stmt.setLong(2, notification.getUserId());
            stmt.setObject(3, notification.getSystemId());
            stmt.setObject(4, notification.getVulnerabilityId());
            stmt.setBoolean(5, notification.isRead());
            stmt.setString(6, notification.getType());
            stmt.setTimestamp(7, notification.getCreatedAt());

            rs = stmt.executeQuery();
            if (rs.next()) {
                notification.setId(rs.getLong("id"));
                return notification;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, rs);
        }
        return null;
    }

    // get notification by id
    public Notification findById(Long id) {
        String sql = "SELECT * FROM notifications WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToNotification(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, rs);
        }
        return null;
    }

    // find notifications by user
    public List<Notification> findByUser(Long userId) {
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC";
        List<Notification> notifications = new ArrayList<>();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, userId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                notifications.add(mapResultSetToNotification(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, rs);
        }
        return notifications;
    }

    // find unread notifications by user
    public List<Notification> findUnreadByUser(Long userId) {
        String sql = "SELECT * FROM notifications WHERE user_id = ? AND is_read = false ORDER BY created_at DESC";
        List<Notification> notifications = new ArrayList<>();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, userId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                notifications.add(mapResultSetToNotification(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, rs);
        }
        return notifications;
    }

    // mark notification as read
    public boolean markAsRead(Long id) {
        String sql = "UPDATE notifications SET is_read = true WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, null);
        }
        return false;
    }

    // delete notification
    public boolean delete(Long id) {
        String sql = "DELETE FROM notifications WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, null);
        }
        return false;
    }

    private Notification mapResultSetToNotification(ResultSet rs) throws SQLException {
        Notification notification = new Notification();
        notification.setId(rs.getLong("id"));
        notification.setMessage(rs.getString("message"));
        notification.setUserId(rs.getLong("user_id"));

        Long systemId = rs.getObject("system_id", Long.class);
        notification.setSystemId(systemId);

        Long vulnerabilityId = rs.getObject("vulnerability_id", Long.class);
        notification.setVulnerabilityId(vulnerabilityId);

        notification.setRead(rs.getBoolean("is_read"));
        notification.setType(rs.getString("type"));
        notification.setCreatedAt(rs.getTimestamp("created_at"));
        return notification;
    }
}
