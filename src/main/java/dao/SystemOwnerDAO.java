package dao;

import config.DatabaseConfig;
import model.SystemImplementation;
import model.User;
import util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Handles database operations related to system ownership assignments.
 */
public class SystemOwnerDAO {
    
    /**
     * Assigns a user as the owner of a specific system implementation.
     *
     * @param userId  ID of the user to assign
     * @param implId  ID of the system implementation
     */
    public void assignOwner(UUID userId, UUID implId) {
        String sql = "INSERT INTO \"systemowner\" (user_id, system_implementation_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, userId);
            stmt.setObject(2, implId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to assign owner", e);
        }
    }
    
    /**
     * Removes an owner from a specific system implementation.
     *
     * @param userId  ID of the user to remove
     * @param implId  ID of the system implementation
     * @return true if an owner was removed, false otherwise
     */
    public boolean removeOwner(UUID userId, UUID implId) {
        String sql = "DELETE FROM \"systemowner\" WHERE user_id = ? AND system_implementation_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, userId);
            stmt.setObject(2, implId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to remove owner", e);
        }
    }
    
    /**
     * Finds all users who are owners of a given system implementation.
     *
     * @param implId ID of the system implementation
     * @return list of users who own it
     */
    public List<User> findOwnersByImplementation(UUID implId) {
        String sql = "SELECT u.* FROM \"useraccount\" u JOIN \"systemowner\" so ON u.id = so.user_id WHERE so.system_implementation_id = ?";
        List<User> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, implId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new UserDAO().mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list owners", e);
        }
        return list;
    }
    
    /**
     * Finds all system implementations that a user owns.
     *
     * @param userId ID of the user
     * @return list of system implementations
     */
    public List<SystemImplementation> findImplementationsByOwner(UUID userId) {
        String sql = "SELECT si.* FROM \"systemimplementation\" si JOIN \"systemowner\" so ON si.id = so.system_implementation_id WHERE so.user_id = ?";
        List<SystemImplementation> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new SystemImplementationDAO().mapResultSetToSystemImplementation(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list implementations", e);
        }
        return list;
    }
    
    /**
     * Finds the owner of a specific system implementation.
     *
     * @param implId ID of the system implementation
     * @return UUID of the owner, or null if no owner found
     */
    public UUID findOwnerBySystemImplementationId(UUID implId) {
        String sql = "SELECT user_id FROM \"systemowner\" WHERE system_implementation_id = ? LIMIT 1";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, implId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return (UUID) rs.getObject("user_id");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find owner", e);
        }
        return null;
    }
}
