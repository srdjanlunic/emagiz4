package dao;

import config.DatabaseConfig;
import model.ITSystem;
import util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SystemDAO {

    // create new system
    public ITSystem create(ITSystem system) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            System.out.println("Creating system: " + system.getName());

            // Generate UUID in Java if not already set
            if (system.getId() == null) {
                system.setId(UUID.randomUUID());
            }

            conn = DatabaseConfig.getConnection();
            System.out.println("Database connection obtained");

            stmt = conn.prepareStatement(
                    "INSERT INTO ITSystem (id, name, vendor, description, created_at) VALUES (?, ?, ?, ?, ?)"
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

    // get system by id
    public ITSystem findById(UUID id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM ITSystem WHERE id = ?");
            stmt.setObject(1, id);
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
            stmt = conn.prepareStatement("SELECT * FROM ITSystem ORDER BY created_at DESC");
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
                    "UPDATE ITSystem SET name = ?, vendor = ?, description = ? WHERE id = ?"
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

    // delete system
    public boolean delete(UUID id) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement("DELETE FROM ITSystem WHERE id = ?");
            stmt.setObject(1, id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, null);
        }
        return false;
    }

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
