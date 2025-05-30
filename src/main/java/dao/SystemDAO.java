package dao;

import config.DatabaseConfig;
import model.ITSystem;
import util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SystemDAO {

    // create new system
    public ITSystem create(ITSystem system) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(
                    "INSERT INTO systems (name, description, version, vendor, department_id, owner_id, " +
                            "data_classification, criticality_level, internet_facing, risk_score, created_at) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id",
                    Statement.RETURN_GENERATED_KEYS
            );

            stmt.setString(1, system.getName());
            stmt.setString(2, system.getDescription());
            stmt.setString(3, system.getVersion());
            stmt.setString(4, system.getVendor());
            stmt.setLong(5, system.getDepartmentId());
            stmt.setLong(6, system.getOwnerId());
            stmt.setString(7, system.getDataClassification());
            stmt.setString(8, system.getCriticalityLevel());
            stmt.setBoolean(9, system.isInternetFacing());
            stmt.setInt(10, system.getRiskScore());
            stmt.setTimestamp(11, system.getCreatedAt());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    system.setId(rs.getLong(1));
                    return system;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, rs);
        }
        return null;
    }

    // get system by id
    public ITSystem findById(Long id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM systems WHERE id = ?");
            stmt.setLong(1, id);
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

    // get all systems
    public List<ITSystem> findAll() {
        List<ITSystem> systems = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM systems ORDER BY created_at DESC");
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

    // update system
    public ITSystem update(ITSystem system) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(
                    "UPDATE systems SET name = ?, description = ?, version = ?, vendor = ?, " +
                            "department_id = ?, owner_id = ?, data_classification = ?, criticality_level = ?, " +
                            "internet_facing = ?, risk_score = ? WHERE id = ?"
            );

            stmt.setString(1, system.getName());
            stmt.setString(2, system.getDescription());
            stmt.setString(3, system.getVersion());
            stmt.setString(4, system.getVendor());
            stmt.setLong(5, system.getDepartmentId());
            stmt.setLong(6, system.getOwnerId());
            stmt.setString(7, system.getDataClassification());
            stmt.setString(8, system.getCriticalityLevel());
            stmt.setBoolean(9, system.isInternetFacing());
            stmt.setInt(10, system.getRiskScore());
            stmt.setLong(11, system.getId());

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

    // delete system
    public boolean delete(Long id) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement("DELETE FROM systems WHERE id = ?");
            stmt.setLong(1, id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, null);
        }
        return false;
    }

    // find systems by department
    public List<ITSystem> findByDepartment(Long departmentId) {
        List<ITSystem> systems = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM systems WHERE department_id = ?");
            stmt.setLong(1, departmentId);
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

    // find systems by owner
    public List<ITSystem> findByOwner(Long ownerId) {
        List<ITSystem> systems = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM systems WHERE owner_id = ?");
            stmt.setLong(1, ownerId);
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

    private ITSystem mapResultSetToSystem(ResultSet rs) throws SQLException {
        ITSystem system = new ITSystem();
        system.setId(rs.getLong("id"));
        system.setName(rs.getString("name"));
        system.setDescription(rs.getString("description"));
        system.setVersion(rs.getString("version"));
        system.setVendor(rs.getString("vendor"));
        system.setDepartmentId(rs.getLong("department_id"));
        system.setOwnerId(rs.getLong("owner_id"));
        system.setDataClassification(rs.getString("data_classification"));
        system.setCriticalityLevel(rs.getString("criticality_level"));
        system.setInternetFacing(rs.getBoolean("internet_facing"));
        system.setRiskScore(rs.getInt("risk_score"));
        system.setCreatedAt(rs.getTimestamp("created_at"));
        return system;
    }
}
