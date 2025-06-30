package dao;

import config.DatabaseConfig;
import model.Department;
import model.User;
import util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DAO for managing user-department relationships in the UserDepartment table.
 */
public class UserDepartmentDAO {
    
    /**
     * Assigns a user to a department.
     *
     * @param userId       ID of the user
     * @param departmentId ID of the department
     */
    public void assignUserToDepartment(UUID userId, UUID departmentId) {
        String sql = "INSERT INTO \"userdepartment\" (user_id, department_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, userId);
            stmt.setObject(2, departmentId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to assign user to department", e);
        }
    }
    
    /**
     * Removes a user from a department.
     *
     * @param userId       ID of the user
     * @param departmentId ID of the department
     * @return true if the record was deleted, false otherwise
     */
    public boolean remove(UUID userId, UUID departmentId) {
        String sql = "DELETE FROM \"userdepartment\" WHERE user_id = ? AND department_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, userId);
            stmt.setObject(2, departmentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to remove user from department", e);
        }
    }
    
    /**
     * Finds all departments that a user belongs to.
     *
     * @param userId ID of the user
     * @return list of Department objects
     */
    public List<Department> findDepartmentsByUser(UUID userId) {
        String sql = "SELECT d.* FROM \"department\" d "
                + "JOIN \"userdepartment\" ud ON d.id = ud.department_id "
                + "WHERE ud.user_id = ?";
        List<Department> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Department d = new Department();
                    d.setId((UUID) rs.getObject("id"));
                    d.setName(rs.getString("name"));
                    d.setDescription(rs.getString("description"));
                    d.setOrganizationId((UUID) rs.getObject("organization_id"));
                    d.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(d);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list departments for user", e);
        }
        return list;
    }
    
    /**
     * Finds all active users assigned to a department.
     *
     * @param departmentId ID of the department
     * @return list of User objects
     */
    public List<User> findUsersByDepartment(UUID departmentId) {
        String sql = "SELECT u.*, r.name AS role_name FROM \"useraccount\" u "
                + "JOIN \"userdepartment\" ud ON u.id = ud.user_id "
                + "LEFT JOIN \"role\" r ON u.role_id = r.id "
                + "WHERE ud.department_id = ? AND u.is_active = true";
        List<User> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, departmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User u = new UserDAO().mapResultSetToUser(rs);
                    list.add(u);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list users for department", e);
        }
        return list;
    }
}
