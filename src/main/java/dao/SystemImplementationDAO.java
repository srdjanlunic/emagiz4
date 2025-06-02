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
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(
                    "INSERT INTO SystemImplementation (system_id, department_id, data_classification, " +
                            "criticality_level, internet_facing, sensitive_customer_data, risk_score, version, " +
                            "environment, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id",
                    Statement.RETURN_GENERATED_KEYS
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
            stmt.setTimestamp(10, implementation.getCreatedAt());
            stmt.setTimestamp(11, implementation.getUpdatedAt());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    implementation.setId((UUID) rs.getObject(1));
                    return implementation;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, rs);
        }
        return null;
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
