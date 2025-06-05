package dao;

import config.DatabaseConfig;
import model.SystemImplementation;
import util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SystemImplementationDAO {

    public SystemImplementation create(SystemImplementation implementation) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            System.out.println("=== SystemImplementationDAO.create - START ===");
            System.out.println("SystemID: " + implementation.getSystemId());
            System.out.println("DepartmentID: " + implementation.getDepartmentId());
            System.out.println("DataClassification: " + implementation.getDataClassification());
            System.out.println("CriticalityLevel: " + implementation.getCriticalityLevel());
            System.out.println("InternetFacing: " + implementation.isInternetFacing());
            System.out.println("SensitiveCustomerData: " + implementation.isSensitiveCustomerData());
            System.out.println("RiskScore: " + implementation.getRiskScore());
            System.out.println("Version: " + implementation.getVersion());
            System.out.println("Environment: " + implementation.getEnvironment());

            conn = DatabaseConfig.getConnection();
            System.out.println("Database connection established");

            // First, let's check if the referenced system and department exist
            System.out.println("Checking if system exists...");
            PreparedStatement checkSystemStmt = conn.prepareStatement("SELECT id FROM ITSystem WHERE id = ?");
            checkSystemStmt.setObject(1, implementation.getSystemId());
            ResultSet systemRs = checkSystemStmt.executeQuery();
            if (!systemRs.next()) {
                System.out.println("❌ System with ID " + implementation.getSystemId() + " does not exist");
                return null;
            }
            systemRs.close();
            checkSystemStmt.close();
            System.out.println("✅ System exists");

            System.out.println("Checking if department exists...");
            PreparedStatement checkDeptStmt = conn.prepareStatement("SELECT id FROM Department WHERE id = ?");
            checkDeptStmt.setObject(1, implementation.getDepartmentId());
            ResultSet deptRs = checkDeptStmt.executeQuery();
            if (!deptRs.next()) {
                System.out.println("❌ Department with ID " + implementation.getDepartmentId() + " does not exist");
                return null;
            }
            deptRs.close();
            checkDeptStmt.close();
            System.out.println("✅ Department exists");

            // Use simpler INSERT without RETURNING clause
            String sql = "INSERT INTO SystemImplementation (id, system_id, department_id, data_classification, " +
                    "criticality_level, internet_facing, sensitive_customer_data, risk_score, version, " +
                    "environment, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            System.out.println("SQL: " + sql);

            // Generate UUID manually
            UUID newId = UUID.randomUUID();
            implementation.setId(newId);
            System.out.println("Generated ID: " + newId);

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

            System.out.println("Statement prepared, executing...");

            int affectedRows = stmt.executeUpdate();
            System.out.println("Affected rows: " + affectedRows);

            if (affectedRows > 0) {
                System.out.println("✅ Implementation created with ID: " + implementation.getId());
                return implementation;
            } else {
                System.out.println("❌ No rows affected");
                return null;
            }
        } catch (SQLException e) {
            System.out.println("❌ SQL Exception in SystemImplementationDAO.create: " + e.getMessage());
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeResources(conn, stmt, rs);
            System.out.println("=== SystemImplementationDAO.create - END ===");
        }
    }

    public SystemImplementation findById(UUID id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM SystemImplementation WHERE id = ?");
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

    public List<SystemImplementation> findAll() {
        List<SystemImplementation> implementations = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM SystemImplementation ORDER BY created_at DESC");
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

    public SystemImplementation update(SystemImplementation implementation) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(
                    "UPDATE SystemImplementation SET system_id = ?, department_id = ?, " +
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

    public boolean delete(UUID id) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement("DELETE FROM SystemImplementation WHERE id = ?");
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

    public List<SystemImplementation> findByDepartment(UUID departmentId) {
        List<SystemImplementation> implementations = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM SystemImplementation WHERE department_id = ?");
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

    public List<SystemImplementation> findBySystem(UUID systemId) {
        List<SystemImplementation> implementations = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM SystemImplementation WHERE system_id = ?");
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

    private SystemImplementation mapResultSetToSystemImplementation(ResultSet rs) throws SQLException {
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
