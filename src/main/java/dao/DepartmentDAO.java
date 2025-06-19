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
        String sql = "INSERT INTO department (id, name, description, organization_id, created_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            department.setId(UUID.randomUUID());
            stmt.setObject(1, department.getId());
            stmt.setString(2, department.getName());
            stmt.setString(3, department.getDescription());
            stmt.setObject(4, department.getOrganizationId());
            stmt.setTimestamp(5, department.getCreatedAt());
            stmt.executeUpdate();
            return department;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Get department by id
    public Department findById(UUID id) {
        String sql = "SELECT * FROM department WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToDepartment(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get all departments
    public List<Department> findAll() {
        String sql = "SELECT * FROM department";
        List<Department> departments = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                departments.add(mapResultSetToDepartment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return departments;
    }

    // Update department
    public Department update(Department department) {
        String sql = "UPDATE department SET name = ?, description = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, department.getName());
            stmt.setString(2, department.getDescription());
            stmt.setObject(3, department.getId());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                return department;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Delete department
    public boolean delete(UUID id) {
        String sql = "DELETE FROM department WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Find departments by organization
    public List<Department> findByOrganization(UUID organizationId) {
        String sql = "SELECT * FROM department WHERE organization_id = ?";
        List<Department> departments = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, organizationId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                departments.add(mapResultSetToDepartment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
