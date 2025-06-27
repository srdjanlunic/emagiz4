package dao;

import config.DatabaseConfig;
import model.ITSystem;
import util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Handles database operations for IT systems.
 */
public class SystemDAO {
    
    /**
     * Creates a new IT system record.
     *
     * @param system the system to insert
     * @return the created system if successful, otherwise null
     */
    public ITSystem create(ITSystem system) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            System.out.println("Creating system: " + system.getName());
            
            // Generate a UUID if none is set
            if (system.getId() == null) {
                system.setId(UUID.randomUUID());
            }
            
            conn = DatabaseConfig.getConnection();
            System.out.println("Database connection obtained");
            
            stmt = conn.prepareStatement(
                    "INSERT INTO \"itsystem\" (id, name, vendor, description, created_at) VALUES (?, ?, ?, ?, ?)"
            );
            
            stmt.setObject(1, system.getId());
            stmt.setString(2, system.getName());
            stmt.setString(3, system.getVendor());
            stmt.setString(4, system.getDescription());
            stmt.setTimestamp(5, system.getCreatedAt());
            
            System.out.println("Executing insert statement with ID: " + system.getId());
            int affectedRows = stmt.executeUpdate();
            System.out.println("Affected rows: " + affectedRows);
            
            if (affectedRows > 0) {
                System.out.println("System created successfully with ID: " + system.getId());
                return system;
            } else {
                System.out.println("No rows affected by insert");
            }
        } catch (SQLException e) {
            System.out.println("SQLException in SystemDAO.create: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Exception in SystemDAO.create: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, null);
        }
        return null;
    }
    
    /**
     * Finds a system by its ID.
     *
     * @param id the system ID
     * @return the system if found, or null
     */
    public ITSystem findById(UUID id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM \"itsystem\" WHERE id = ?");
            stmt.setObject(1, id);
            rs = stmt.executeQuery();
            System.out.println(rs);
            
            if (rs.next()) {
                return mapResultSetToSystem(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, rs);
        }
        return null;
    }
    
    /**
     * Finds a system by its name and vendor.
     *
     * @param name the system name
     * @param vendor the system vendor
     * @return the system if found, or null
     */
    public ITSystem findByNameAndVendor(String name, String vendor) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM \"itsystem\" WHERE name = ? AND vendor = ?");
            stmt.setString(1, name);
            stmt.setString(2, vendor);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToSystem(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, rs);
        }
        return null;
    }
    
    /**
     * Returns all systems in the database.
     *
     * @return list of IT systems
     */
    public List<ITSystem> findAll() {
        List<ITSystem> systems = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM \"itsystem\" ORDER BY created_at DESC");
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                systems.add(mapResultSetToSystem(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, rs);
        }
        return systems;
    }

    public List<ITSystem> findAll(int page, int pageSize) {
        List<ITSystem> systems = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM \"itsystem\" ORDER BY created_at DESC LIMIT ? OFFSET ?");
            stmt.setInt(1, pageSize);
            stmt.setInt(2, (page - 1) * pageSize);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                systems.add(mapResultSetToSystem(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, rs);
        }
        return systems;
    }

    public int countAll() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement("SELECT COUNT(*) FROM \"itsystem\"");
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, rs);
        }
        return 0;
    }
    
    /**
     * Updates an existing system's data.
     *
     * @param system the updated system
     * @return the updated system if successful, otherwise null
     */
    public ITSystem update(ITSystem system) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(
                    "UPDATE \"itsystem\" SET name = ?, vendor = ?, description = ? WHERE id = ?"
            );
            
            stmt.setString(1, system.getName());
            stmt.setString(2, system.getVendor());
            stmt.setString(3, system.getDescription());
            stmt.setObject(4, system.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                return system;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, null);
        }
        return null;
    }
    
    /**
     * Deletes a system by its ID.
     *
     * @param id the system ID
     * @return true if deleted successfully
     */
    public boolean delete(UUID id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement("DELETE FROM \"itsystem\" WHERE id = ?");
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
     * Maps a result set row to an ITSystem object.
     *
     * @param rs the result set
     * @return the mapped system
     * @throws SQLException if data read fails
     */
    private ITSystem mapResultSetToSystem(ResultSet rs) throws SQLException {
        ITSystem system = new ITSystem();
        system.setId((UUID) rs.getObject("id"));
        system.setName(rs.getString("name"));
        system.setVendor(rs.getString("vendor"));
        system.setDescription(rs.getString("description"));
        system.setCreatedAt(rs.getTimestamp("created_at"));
        return system;
    }
}
