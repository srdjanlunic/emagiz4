package dao;

import config.DatabaseConfig;
import model.Department;
import util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DepartmentDAO {

    // Create new department
    public Department create(Department department) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConfig.getConnection();

            // Generate UUID if not set
            if (department.getId() == null) {
                department.setId(UUID.randomUUID());
            }

            // Set created timestamp if not set
            if (department.getCreatedAt() == null) {
                department.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            }

            String sql = "INSERT INTO Department (id, name, description, organization_id, created_at) VALUES (?, ?, ?, ?, ?)";

            stmt = conn.prepareStatement(sql);
            stmt.setObject(1, department.getId());
            stmt.setString(2, department.getName());
            stmt.setString(3, department.getDescription());
            stmt.setObject(4, department.getOrganizationId());
            stmt.setTimestamp(5, department.getCreatedAt());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                return department;
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeResources(conn, stmt, rs);
        }
    }

    // Get department by id
    public Department findById(UUID id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM Department WHERE id = ?");
            stmt.setObject(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToDepartment(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, rs);
        }
        return null;
    }

    // Get all departments
    public List<Department> findAll() {
        List<Department> departments = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM Department ORDER BY created_at DESC");
            rs = stmt.executeQuery();

            while (rs.next()) {
                departments.add(mapResultSetToDepartment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, rs);
        }
        return departments;
    }

    // Update department
    public Department update(Department department) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(
                    "UPDATE Department SET name = ?, description = ?, organization_id = ? WHERE id = ?"
            );

            stmt.setString(1, department.getName());
            stmt.setString(2, department.getDescription());
            stmt.setObject(3, department.getOrganizationId());
            stmt.setObject(4, department.getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                return department;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, null);
        }
        return null;
    }

    // Delete department
    public boolean delete(UUID id) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement("DELETE FROM Department WHERE id = ?");
            stmt.setObject(1, id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, null);
        }
        return false;
    }

    // Find departments by organization
    public List<Department> findByOrganization(UUID organizationId) {
        List<Department> departments = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM Department WHERE organization_id = ?");
            stmt.setObject(1, organizationId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                departments.add(mapResultSetToDepartment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, rs);
        }
        return departments;
    }

    private Department mapResultSetToDepartment(ResultSet rs) throws SQLException {
        Department department = new Department();
        department.setId((UUID) rs.getObject("id"));
        department.setName(rs.getString("name"));
        department.setDescription(rs.getString("description"));
        department.setOrganizationId((UUID) rs.getObject("organization_id"));
        department.setCreatedAt(rs.getTimestamp("created_at"));
        return department;
    }
}
