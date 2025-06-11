package dao;

import config.DatabaseConfig;
import model.Role;
import util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RoleDAO {

    public Role create(Role role) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(
                    "INSERT INTO Role (name, description, created_at) VALUES (?, ?, ?) RETURNING id",
                    Statement.RETURN_GENERATED_KEYS
            );

            stmt.setString(1, role.getName());
            stmt.setString(2, role.getDescription());
            stmt.setTimestamp(3, role.getCreatedAt());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    role.setId((UUID) rs.getObject(1));
                    return role;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, rs);
        }
        return null;
    }

    public List<Role> findAll() {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT * FROM Role ORDER BY name";
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

    public Role findByName(String name) {
        Role role = null;
        String sql = "SELECT * FROM Role WHERE LOWER(name) = LOWER(?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    role = mapResultSetToRole(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return role;
    }

    public Role findById(UUID id) {
        Role role = null;
        String sql = "SELECT * FROM Role WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    role = mapResultSetToRole(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return role;
    }

    public Role update(Role role) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(
                    "UPDATE Role SET name = ?, description = ? WHERE id = ?"
            );

            stmt.setString(1, role.getName());
            stmt.setString(2, role.getDescription());
            stmt.setObject(3, role.getId());

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

    public boolean delete(UUID id) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement("DELETE FROM Role WHERE id = ?");
            stmt.setObject(1, id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, null);
        }
        return false;
    }

    private Role mapResultSetToRole(ResultSet rs) throws SQLException {
        Role role = new Role();
        role.setId((UUID) rs.getObject("id"));
        role.setName(rs.getString("name"));
        role.setDescription(rs.getString("description"));
        role.setCreatedAt(rs.getTimestamp("created_at"));
        return role;
    }
}
