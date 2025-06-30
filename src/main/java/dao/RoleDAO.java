package dao;

import config.DatabaseConfig;
import model.Role;
import util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Manages database operations related to roles.
 */
public class RoleDAO {
    
    /**
     * Creates a new role in the database.
     *
     * @param role the role to add
     * @return the saved role with its generated ID
     */
    public Role create(Role role) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(
                    "INSERT INTO \"role\" (name, description, created_at) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );

            stmt.setString(1, role.getName());
            stmt.setString(2, role.getDescription());
            stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        role.setId(UUID.fromString(generatedKeys.getString(1)));
                    }
                }
                return role;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, null);
        }
        return null;
    }
    
    /**
     * Returns all roles sorted by name.
     *
     * @return list of roles
     */
    public List<Role> findAll() {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT * FROM \"role\" ORDER BY name";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                roles.add(mapResultSetToRole(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles;
    }
    
    /**
     * Finds a role by its name (case-insensitive).
     *
     * @param roleName the role name
     * @return the matching role or null
     */
    public Role findByName(String roleName) {
        String sql = "SELECT * FROM \"role\" WHERE LOWER(name) = LOWER(?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, roleName);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToRole(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, rs);
        }
        return null;
    }
    
    /**
     * Finds a role by its ID.
     *
     * @param id the role ID
     * @return the role or null
     */
    public Role findById(UUID id) {
        Role role = null;
        String sql = "SELECT * FROM \"role\" WHERE id = ?";
        System.out.println("RoleDAO.findById called with ID: " + id);
        System.out.println("SQL: " + sql);
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Try UUID first, then fall back to integer mapping if needed
            try {
                stmt.setString(1, id.toString());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        role = mapResultSetToRole(rs);
                        System.out.println("Role found: " + role.getName() + " (ID: " + role.getId() + ")");
                    } else {
                        System.out.println("No role found with UUID: " + id);
                        // Try mapping UUID to integer for compatibility
                        role = findByMappedId(id, conn);
                    }
                }
            } catch (SQLException e) {
                System.out.println("Error finding role by UUID, trying integer mapping: " + e.getMessage());
                role = findByMappedId(id, conn);
            }
        } catch (SQLException e) {
            System.out.println("Error finding role by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return role;
    }
    
    /**
     * Helper method to find role by mapped integer ID when UUID lookup fails
     *
     * @param uuidId the UUID to map to integer
     * @param conn the database connection
     * @return the role or null
     */
    private Role findByMappedId(UUID uuidId, Connection conn) {
        // Map UUIDs to integer IDs for backward compatibility
        Integer mappedId = null;
        String uuidStr = uuidId.toString();
        switch (uuidStr) {
            case "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15":
                mappedId = 1; // ADMIN
                break;
            case "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12":
                mappedId = 2; // SYSTEM_OWNER
                break;
            case "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13":
                mappedId = 3; // SECURITY_OFFICER
                break;
            case "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14":
                mappedId = 4; // TECHNICAL_EXPERT
                break;
        }
        
        if (mappedId != null) {
            String sql = "SELECT * FROM \"role\" WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, mappedId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Role role = mapResultSetToRole(rs);
                        // Convert the integer ID back to UUID for consistency
                        role.setId(uuidId);
                        System.out.println("Role found via integer mapping: " + role.getName() + " (Mapped ID: " + mappedId + " -> UUID: " + uuidId + ")");
                        return role;
                    }
                }
            } catch (SQLException e) {
                System.out.println("Error finding role by mapped integer ID: " + e.getMessage());
            }
        }
        
        return null;
    }
    
    /**
     * Updates a role's name and description.
     *
     * @param role the updated role
     * @return the updated role or null if failed
     */
    public Role update(Role role) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(
                    "UPDATE \"role\" SET name = ?, description = ? WHERE id = ?"
            );
            
            stmt.setString(1, role.getName());
            stmt.setString(2, role.getDescription());
            stmt.setString(3, role.getId().toString());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                return role;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, null);
        }
        return null;
    }
    
    /**
     * Deletes a role from the database.
     *
     * @param id the role ID
     * @return true if deleted successfully
     */
    public boolean delete(UUID id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement("DELETE FROM \"role\" WHERE id = ?");
            stmt.setString(1, id.toString());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, null);
        }
        return false;
    }
    
    /**
     * Converts a database row into a Role object.
     *
     * @param rs the result set
     * @return a Role instance
     * @throws SQLException if column read fails
     */
    private Role mapResultSetToRole(ResultSet rs) throws SQLException {
        Role role = new Role();
        role.setId(UUID.fromString(rs.getString("id")));
        role.setName(rs.getString("name"));
        
        // Only set description if the column exists
        if (hasColumn(rs, "description")) {
            role.setDescription(rs.getString("description"));
        } else {
            role.setDescription("No description"); // Default value
        }
        
        if (hasColumn(rs, "created_at")) {
            role.setCreatedAt(rs.getTimestamp("created_at"));
        }
        return role;
    }
    
    /**
     * Checks if a result set has a specific column.
     * 
     * @param rs the result set
     * @param columnName the column name to check
     * @return true if column exists
     * @throws SQLException if metadata access fails
     */
    private boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            if (columnName.equalsIgnoreCase(metaData.getColumnName(i))) {
                return true;
            }
        }
        return false;
    }
}
