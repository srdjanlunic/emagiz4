package dao;

import config.DatabaseConfig;
import model.Notification;
import util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Handles database operations related to notifications.
 */
public class NotificationDAO {
    
    /**
     * Creates a new notification in the database.
     *
     * @param notification the notification to add
     * @return the saved notification with generated ID
     */
    public Notification create(Notification notification) {
        String sql = "INSERT INTO Notification (id, user_id, match_id, system_id, vulnerability_id, message, type, priority, is_read, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            
            // Set all values for insert
            stmt.setObject(1, UUID.randomUUID());
            stmt.setObject(2, notification.getUserId());
            stmt.setObject(3, notification.getMatchId());
            stmt.setObject(4, notification.getSystemId());
            stmt.setObject(5, notification.getVulnerabilityId());
            stmt.setString(6, notification.getMessage());
            stmt.setString(7, notification.getType());
            stmt.setString(8, notification.getPriority());
            stmt.setBoolean(9, notification.isRead());
            stmt.setTimestamp(10, notification.getCreatedAt());
            
            rs = stmt.executeQuery();
            if (rs.next()) {
                notification.setId((UUID) rs.getObject("id"));
                return notification;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, rs);
        }
        return null;
    }
    
    /**
     * Finds a notification by its ID.
     *
     * @param id the notification ID
     * @return the notification if found, else null
     */
    public Notification findById(UUID id) {
        String sql = "SELECT * FROM Notification WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setObject(1, id);
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
    
    /**
     * Returns all notifications for a specific user.
     *
     * @param userId the user's ID
     * @return list of notifications
     */
    public List<Notification> findByUser(UUID userId) {
        String sql = "SELECT * FROM Notification WHERE user_id = ? ORDER BY created_at DESC";
        List<Notification> notifications = new ArrayList<>();
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setObject(1, userId);
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
    
    /**
     * Returns unread notifications for a specific user.
     *
     * @param userId the user's ID
     * @return list of unread notifications
     */
    public List<Notification> findUnreadByUser(UUID userId) {
        String sql = "SELECT * FROM Notification WHERE user_id = ? AND is_read = false ORDER BY created_at DESC";
        List<Notification> notifications = new ArrayList<>();
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setObject(1, userId);
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
    
    /**
     * Marks a notification as read.
     *
     * @param id the notification ID
     * @return true if updated, false otherwise
     */
    public boolean markAsRead(UUID id) {
        String sql = "UPDATE Notification SET is_read = true, read_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setObject(1, id);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, null);
        }
        return false;
    }
    
    /**
     * Deletes a notification by ID.
     *
     * @param id the notification ID
     * @return true if deleted, false otherwise
     */
    public boolean delete(UUID id) {
        String sql = "DELETE FROM Notification WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setObject(1, id);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, null);
        }
        return false;
    }
    
    /**
     * Maps a row from the result set to a Notification object.
     *
     * @param rs the result set
     * @return the mapped Notification
     * @throws SQLException if a column read fails
     */
    private Notification mapResultSetToNotification(ResultSet rs) throws SQLException {
        Notification notification = new Notification();
        notification.setId((UUID) rs.getObject("id"));
        notification.setUserId((UUID) rs.getObject("user_id"));
        notification.setMatchId((UUID) rs.getObject("match_id"));
        notification.setSystemId((UUID) rs.getObject("system_id"));
        notification.setVulnerabilityId((UUID) rs.getObject("vulnerability_id"));
        notification.setMessage(rs.getString("message"));
        notification.setType(rs.getString("type"));
        notification.setPriority(rs.getString("priority"));
        notification.setRead(rs.getBoolean("is_read"));
        notification.setReadAt(rs.getTimestamp("read_at"));
        notification.setCreatedAt(rs.getTimestamp("created_at"));
        return notification;
    }
}
