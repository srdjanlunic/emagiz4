package dao;

import config.DatabaseConfig;
import model.Organization;
import util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrganizationDAO {

    public Organization create(Organization organization) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(
                    "INSERT INTO Organization (name, created_at) VALUES (?, ?) RETURNING id",
                    Statement.RETURN_GENERATED_KEYS
            );

            stmt.setString(1, organization.getName());
            stmt.setTimestamp(2, organization.getCreatedAt());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    organization.setId((UUID) rs.getObject(1));
                    return organization;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, rs);
        }
        return null;
    }

    public Organization findById(UUID id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM Organization WHERE id = ?");
            stmt.setObject(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToOrganization(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, rs);
        }
        return null;
    }

    public List<Organization> findAll() {
        List<Organization> organizations = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM Organization ORDER BY created_at DESC");
            rs = stmt.executeQuery();

            while (rs.next()) {
                organizations.add(mapResultSetToOrganization(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, rs);
        }
        return organizations;
    }

    public Organization update(Organization organization) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(
                    "UPDATE Organization SET name = ? WHERE id = ?"
            );

            stmt.setString(1, organization.getName());
            stmt.setObject(2, organization.getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                return organization;
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
            stmt = conn.prepareStatement("DELETE FROM Organization WHERE id = ?");
            stmt.setObject(1, id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, null);
        }
        return false;
    }

    private Organization mapResultSetToOrganization(ResultSet rs) throws SQLException {
        Organization organization = new Organization();
        organization.setId((UUID) rs.getObject("id"));
        organization.setName(rs.getString("name"));
        organization.setCreatedAt(rs.getTimestamp("created_at"));
        return organization;
    }
}
