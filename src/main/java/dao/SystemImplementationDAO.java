package dao;

import config.DatabaseConfig;
import model.SystemImplementation;
import util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Handles database operations for system implementations (i.e. specific deployments of systems in departments).
 */
public class SystemImplementationDAO {
    
    /**
     * Creates a new SystemImplementation record after verifying references exist.
     *
     * @param implementation the implementation to insert
     * @return the created implementation, or null if insert failed
     */
    public SystemImplementation create(SystemImplementation implementation) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            System.out.println("=== SystemImplementationDAO.create - START ===");
            
            conn = DatabaseConfig.getConnection();
            
            // Validate existence of referenced ITSystem
            PreparedStatement checkSystemStmt = conn.prepareStatement("SELECT id FROM itsystem WHERE id = ?");
            checkSystemStmt.setObject(1, implementation.getSystemId());
            ResultSet systemRs = checkSystemStmt.executeQuery();
            if (!systemRs.next()) {
                System.out.println("System not found");
                return null;
            }
            systemRs.close();
            checkSystemStmt.close();
            
            // Insert the implementation
            String sql = "INSERT INTO systemimplementation (id, system_id, department_id, data_classification, " +
                    "criticality_level, internet_facing, sensitive_customer_data, risk_score, version, " +
                    "environment, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            UUID newId = UUID.randomUUID();
            implementation.setId(newId);
            
            stmt = conn.prepareStatement(sql);
            stmt.setObject(1, implementation.getId());
            stmt.setObject(2, implementation.getSystemId());
            stmt.setObject(3, implementation.getDepartmentId());
            stmt.setString(4, implementation.getDataClassification());
            stmt.setString(5, implementation.getCriticalityLevel());
            stmt.setBoolean(6, implementation.isInternetFacing());
            stmt.setBoolean(7, implementation.isSensitiveCustomerData());
            stmt.setInt(8, implementation.getRiskScore());
            stmt.setString(9, implementation.getVersion());
            stmt.setString(10, implementation.getEnvironment());
            stmt.setTimestamp(11, implementation.getCreatedAt());
            stmt.setTimestamp(12, implementation.getUpdatedAt());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Implementation created");
                return implementation;
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception in create: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, rs);
            System.out.println("=== SystemImplementationDAO.create - END ===");
        }
        return null;
    }
    
    /**
     * Finds a system implementation by its ID.
     */
    public SystemImplementation findById(UUID id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM systemimplementation WHERE id = ?");
            stmt.setObject(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToSystemImplementation(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error in findById: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, rs);
        }
        return null;
    }
    
    /**
     * Returns all system implementations.
     */
    public List<SystemImplementation> findAll() {
        List<SystemImplementation> implementations = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM systemimplementation ORDER BY created_at DESC");
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                implementations.add(mapResultSetToSystemImplementation(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error in findAll: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, rs);
        }
        return implementations;
    }
    
    /**
     * Updates an existing system implementation.
     */
    public SystemImplementation update(SystemImplementation implementation) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(
                    "UPDATE systemimplementation SET system_id = ?, department_id = ?, " +
                            "data_classification = ?, criticality_level = ?, internet_facing = ?, " +
                            "sensitive_customer_data = ?, risk_score = ?, version = ?, environment = ?, " +
                            "updated_at = ? WHERE id = ?"
            );
            
            stmt.setObject(1, implementation.getSystemId());
            stmt.setObject(2, implementation.getDepartmentId());
            stmt.setString(3, implementation.getDataClassification());
            stmt.setString(4, implementation.getCriticalityLevel());
            stmt.setBoolean(5, implementation.isInternetFacing());
            stmt.setBoolean(6, implementation.isSensitiveCustomerData());
            stmt.setInt(7, implementation.getRiskScore());
            stmt.setString(8, implementation.getVersion());
            stmt.setString(9, implementation.getEnvironment());
            stmt.setTimestamp(10, implementation.getUpdatedAt());
            stmt.setObject(11, implementation.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                return implementation;
            }
        } catch (SQLException e) {
            System.out.println("Error in update: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, null);
        }
        return null;
    }
    
    /**
     * Deletes a system implementation by ID.
     */
    public boolean delete(UUID id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement("DELETE FROM systemimplementation WHERE id = ?");
            stmt.setObject(1, id);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error in delete: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, null);
        }
        return false;
    }
    
    /**
     * Gets all implementations for a specific department.
     */
    public List<SystemImplementation> findByDepartment(UUID departmentId) {
        List<SystemImplementation> implementations = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM systemimplementation WHERE department_id = ?");
            stmt.setObject(1, departmentId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                implementations.add(mapResultSetToSystemImplementation(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error in findByDepartment: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, rs);
        }
        return implementations;
    }
    
    /**
     * Gets all implementations for a specific system.
     */
    public List<SystemImplementation> findBySystem(UUID systemId) {
        List<SystemImplementation> implementations = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM systemimplementation WHERE system_id = ?");
            stmt.setObject(1, systemId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                implementations.add(mapResultSetToSystemImplementation(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error in findBySystem: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, rs);
        }
        return implementations;
    }
    
    /**
     * Finds all system IDs that are linked to a given vulnerability.
     */
    public List<String> findSystemIdsByVulnerabilityId(UUID vulnerabilityId) {
        List<String> systemIds = new ArrayList<>();
        String sql = "SELECT DISTINCT si.system_id FROM systemimplementation si " +
                "JOIN VulnerabilityMatch vm ON si.id = vm.system_implementation_id " +
                "WHERE vm.vulnerability_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setObject(1, vulnerabilityId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                systemIds.add(((UUID) rs.getObject("system_id")).toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return systemIds;
    }
    
    /**
     * Converts a result set row into a SystemImplementation object.
     */
    public SystemImplementation mapResultSetToSystemImplementation(ResultSet rs) throws SQLException {
        SystemImplementation implementation = new SystemImplementation();
        implementation.setId((UUID) rs.getObject("id"));
        implementation.setSystemId((UUID) rs.getObject("system_id"));
        implementation.setDepartmentId((UUID) rs.getObject("department_id"));
        implementation.setDataClassification(rs.getString("data_classification"));
        implementation.setCriticalityLevel(rs.getString("criticality_level"));
        implementation.setInternetFacing(rs.getBoolean("internet_facing"));
        implementation.setSensitiveCustomerData(rs.getBoolean("sensitive_customer_data"));
        implementation.setRiskScore(rs.getInt("risk_score"));
        implementation.setVersion(rs.getString("version"));
        implementation.setEnvironment(rs.getString("environment"));
        implementation.setCreatedAt(rs.getTimestamp("created_at"));
        implementation.setUpdatedAt(rs.getTimestamp("updated_at"));
        return implementation;
    }
}
