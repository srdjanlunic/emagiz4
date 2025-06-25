package dao;

import config.DatabaseConfig;
import dto.UserDto;
import model.User;
import util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Handles database operations related to User entities.
 */
public class UserDAO {
    
    /**
     * Finds a user by username for login purposes.
     *
     * @param username the username of the user
     * @return the matching User, or null if not found
     */
    public User findByUsername(String username) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(
                    "SELECT * FROM UserAccount WHERE username = ? AND is_active = true"
            );
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, rs);
        }
        return null;
    }
    
    /**
     * Inserts a new user record into the database.
     *
     * @param user the user to create
     * @return the created user with ID set, or null if creation failed
     */
    public User create(User user) {
        String sql = "INSERT INTO useraccount (id, username, password, email, first_name, last_name, role_id, is_active, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            user.setId(UUID.randomUUID());
            stmt.setObject(1, user.getId());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getFirstName());
            stmt.setString(6, user.getLastName());
            stmt.setObject(7, user.getRoleId());
            stmt.setBoolean(8, user.isActive());
            stmt.setTimestamp(9, new Timestamp(System.currentTimeMillis()));
            
            stmt.executeUpdate();
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Retrieves a user by their ID.
     *
     * @param id the user's UUID
     * @return the user if found, otherwise null
     */
    public User findById(UUID id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement("SELECT u.*, r.name as role_name FROM UserAccount u LEFT JOIN Role r ON u.role_id = r.id WHERE u.id = ?");
            stmt.setObject(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, rs);
        }
        return null;
    }
    
    /**
     * Gets a list of all active users with basic details.
     *
     * @return list of UserDto with basic information
     */
    public List<UserDto> findAll() {
        String sql = "SELECT u.id, u.username, u.email, r.name as role_name " +
                "FROM useraccount u " +
                "LEFT JOIN role r ON u.role_id = r.id WHERE is_active = true";
        List<UserDto> users = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                users.add(new UserDto(
                        (UUID) rs.getObject("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("role_name"),
                        null // Department name will be fetched separately
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
    
    /**
     * Updates an existing user's information.
     *
     * @param user the user with updated details
     * @return the updated user, or null if update failed
     */
    public User update(User user) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(
                    "UPDATE UserAccount SET username = ?, email = ?, first_name = ?, last_name = ?, " +
                            "role_id = ?, organization_id = ?, is_active = ? WHERE id = ?"
            );
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getFirstName());
            stmt.setString(4, user.getLastName());
            stmt.setObject(5, user.getRoleId());
            stmt.setObject(6, user.getOrganizationId());
            stmt.setBoolean(7, user.isActive());
            stmt.setObject(8, user.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, null);
        }
        return null;
    }
    
    /**
     * Marks a user as inactive (soft delete).
     *
     * @param id the user's UUID
     * @return true if user was marked inactive, false otherwise
     */
    public boolean delete(UUID id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement("UPDATE UserAccount SET is_active = false WHERE id = ?");
            stmt.setObject(1, id);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, null);
        }
        return false;
    }
    
    /**
     * Finds all users in a specific department.
     *
     * @param departmentId the UUID of the department
     * @return list of users in the department
     */
    public List<User> findByDepartment(UUID departmentId) {
        List<User> users = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(
                    "SELECT u.* FROM UserAccount u " +
                            "JOIN UserDepartment ud ON u.id = ud.user_id " +
                            "WHERE ud.department_id = ? AND u.is_active = true"
            );
            stmt.setObject(1, departmentId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, rs);
        }
        return users;
    }
    
    /**
     * Finds all users by a given role.
     *
     * @param roleId the UUID of the role
     * @return list of users with the given role
     */
    public List<User> findByRole(UUID roleId) {
        List<User> users = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM UserAccount WHERE role_id = ? AND is_active = true");
            stmt.setObject(1, roleId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, rs);
        }
        return users;
    }
    
    /**
     * Finds the first user with a specific role ID.
     *
     * @param roleId the UUID of the role
     * @return the first matching user, or null if none found
     */
    public User findFirstByRoleId(UUID roleId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM UserAccount WHERE role_id = ? AND is_active = true LIMIT 1");
            stmt.setObject(1, roleId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, rs);
        }
        return null;
    }
    
    /**
     * Converts a ResultSet row into a User object.
     *
     * @param rs the SQL ResultSet
     * @return the corresponding User object
     * @throws SQLException if a column access error occurs
     */
    public User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId((UUID) rs.getObject("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        if (hasColumn(rs, "role_id")) {
            user.setRoleId((UUID) rs.getObject("role_id"));
        }
        if (hasColumn(rs, "organization_id")) {
            user.setOrganizationId((UUID) rs.getObject("organization_id"));
        }
        user.setActive(rs.getBoolean("is_active"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        if (hasColumn(rs, "updated_at")) {
            user.setUpdatedAt(rs.getTimestamp("updated_at"));
        }
        if (hasColumn(rs, "role_name")) {
            user.setRoleName(rs.getString("role_name"));
        }
        return user;
    }
    
    /**
     * Checks if a column exists in a ResultSet.
     *
     * @param rs the ResultSet to inspect
     * @param columnName the column name to look for
     * @return true if column exists, false otherwise
     * @throws SQLException if ResultSet metadata access fails
     */
    private boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int columns = rsmd.getColumnCount();
        for (int x = 1; x <= columns; x++) {
            if (columnName.equals(rsmd.getColumnName(x))) {
                return true;
            }
        }
        return false;
    }
}
