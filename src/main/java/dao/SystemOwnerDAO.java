// dao/SystemOwnerDAO.java
package dao;

import config.DatabaseConfig;
import model.SystemImplementation;
import model.User;
import util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SystemOwnerDAO {
    public void assignOwner(UUID userId, UUID implId) {
        String sql = "INSERT INTO SystemOwner (user_id, system_implementation_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, userId);
            stmt.setObject(2, implId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to assign owner", e);
        }
    }

    public boolean removeOwner(UUID userId, UUID implId) {
        String sql = "DELETE FROM SystemOwner WHERE user_id = ? AND system_implementation_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, userId);
            stmt.setObject(2, implId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to remove owner", e);
        }
    }

    public List<User> findOwnersByImplementation(UUID implId) {
        String sql = "SELECT u.* FROM UserAccount u JOIN SystemOwner so ON u.id = so.user_id WHERE so.system_implementation_id = ?";
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

    public List<SystemImplementation> findImplementationsByOwner(UUID userId) {
        String sql = "SELECT si.* FROM SystemImplementation si JOIN SystemOwner so ON si.id = so.system_implementation_id WHERE so.user_id = ?";
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
}
