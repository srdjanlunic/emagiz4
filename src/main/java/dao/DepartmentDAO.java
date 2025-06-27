package dao;

import config.DatabaseConfig;
import model.Department;
import util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Handles database operations for departments.
 */
public class DepartmentDAO {
    
    /**
     * Adds a new department to the database.
     *
     * @param department the department to create
     * @return the created department with ID set
     */
    public Department create(Department department) {
        String sql = "INSERT INTO department (id, name, description, organization_id, created_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            department.setId(UUID.randomUUID()); // generate new ID
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
    
    /**
     * Finds a department by its ID.
     *
     * @param id department ID
     * @return the department or null
     */
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
    
    /**
     * Finds a department by its name.
     *
     * @param name department name
     * @return the department or null
     */
    public Department findByName(String name) {
        String sql = "SELECT * FROM department WHERE name = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToDepartment(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Returns all departments.
     *
     * @return list of departments
     */
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
    
    /**
     * Updates a department's name and description.
     *
     * @param department the updated department
     * @return the updated department, or null if failed
     */
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
    
    /**
     * Deletes a department by ID.
     *
     * @param id department ID
     * @return true if deleted, false otherwise
     */
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
    
    /**
     * Finds all departments under a specific organization.
     *
     * @param organizationId the organization ID
     * @return list of departments
     */
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
    
    /**
     * Maps a database row to a Department object.
     *
     * @param rs result set row
     * @return Department object
     * @throws SQLException on error
     */
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
