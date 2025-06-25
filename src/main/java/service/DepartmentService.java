package service;

import dao.DepartmentDAO;
import model.Department;

import java.util.List;
import java.util.UUID;

/**
 * Service layer for Department-related business logic and data access coordination.
 */
public class DepartmentService {
    private DepartmentDAO departmentDAO;
    
    /**
     * Constructs a DepartmentService instance and initializes the DAO.
     */
    public DepartmentService() {
        this.departmentDAO = new DepartmentDAO();
    }
    
    /**
     * Validates the department name to prevent injection or XSS.
     * Allows letters, digits, spaces, dash, underscore, and dots.
     *
     * @param name the department name to validate
     * @return true if valid; false otherwise
     */
    boolean isValidDepartmentName(String name) {
        if (name == null || name.isBlank()) return false;
        return name.matches("[a-zA-Z0-9 ._-]+");
    }
    
    /**
     * Creates a new department.
     *
     * @param department the Department object to create
     * @return the created Department with generated ID and fields, or null if invalid input
     */
    public Department createDepartment(Department department) {
        if (department == null || !isValidDepartmentName(department.getName())) {
            return null;
        }
        return departmentDAO.create(department);
    }
    
    /**
     * Retrieves all departments.
     *
     * @return a List of all Department objects
     */
    public List<Department> getAllDepartments() {
        return departmentDAO.findAll();
    }
    
    /**
     * Retrieves a department by its unique ID.
     *
     * @param id the UUID of the department
     * @return the Department object if found; otherwise null
     */
    public Department getDepartmentById(UUID id) {
        return departmentDAO.findById(id);
    }
    
    /**
     * Updates an existing department.
     *
     * @param department the Department object with updated information
     * @return the updated Department object, or null if update failed or invalid input
     */
    public Department updateDepartment(Department department) {
        if (department == null || !isValidDepartmentName(department.getName())) {
            return null;
        }
        return departmentDAO.update(department);
    }
    
    /**
     * Deletes a department by its ID.
     *
     * @param id the UUID of the department to delete
     * @return true if the department was deleted; false otherwise
     */
    public boolean deleteDepartment(UUID id) {
        return departmentDAO.delete(id);
    }
    
    /**
     * Retrieves all departments belonging to a specific organization.
     *
     * @param organizationId the UUID of the organization
     * @return a List of Department objects for the given organization
     */
    public List<Department> getDepartmentsByOrganization(UUID organizationId) {
        return departmentDAO.findByOrganization(organizationId);
    }
}
