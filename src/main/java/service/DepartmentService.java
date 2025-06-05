package service;

import dao.DepartmentDAO;
import model.Department;
import java.util.List;
import java.util.UUID;

public class DepartmentService {
    private DepartmentDAO departmentDAO;

    public DepartmentService() {
        this.departmentDAO = new DepartmentDAO();
    }

    public Department createDepartment(Department department) {
        try {
            // Validate required fields
            if (department.getName() == null || department.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("Department name is required");
            }

            if (department.getOrganizationId() == null) {
                throw new IllegalArgumentException("Organization ID is required");
            }

            // Set default values if not provided
            if (department.getId() == null) {
                department.setId(UUID.randomUUID());
            }

            if (department.getCreatedAt() == null) {
                department.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
            }

            return departmentDAO.create(department);
        } catch (Exception e) {
            System.out.println("Error in DepartmentService.createDepartment: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create department: " + e.getMessage(), e);
        }
    }

    public List<Department> getAllDepartments() {
        try {
            return departmentDAO.findAll();
        } catch (Exception e) {
            System.out.println("Error in DepartmentService.getAllDepartments: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve departments: " + e.getMessage(), e);
        }
    }

    public Department getDepartmentById(UUID id) {
        try {
            return departmentDAO.findById(id);
        } catch (Exception e) {
            System.out.println("Error in DepartmentService.getDepartmentById: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve department: " + e.getMessage(), e);
        }
    }

    public Department updateDepartment(Department department) {
        try {
            return departmentDAO.update(department);
        } catch (Exception e) {
            System.out.println("Error in DepartmentService.updateDepartment: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to update department: " + e.getMessage(), e);
        }
    }

    public boolean deleteDepartment(UUID id) {
        try {
            return departmentDAO.delete(id);
        } catch (Exception e) {
            System.out.println("Error in DepartmentService.deleteDepartment: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to delete department: " + e.getMessage(), e);
        }
    }

    public List<Department> getDepartmentsByOrganization(UUID organizationId) {
        try {
            return departmentDAO.findByOrganization(organizationId);
        } catch (Exception e) {
            System.out.println("Error in DepartmentService.getDepartmentsByOrganization: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve departments by organization: " + e.getMessage(), e);
        }
    }
}
