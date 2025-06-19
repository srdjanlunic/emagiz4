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
        // Add any business logic here before creating the department
        return departmentDAO.create(department);
    }

    public List<Department> getAllDepartments() {
        return departmentDAO.findAll();
    }

    public Department getDepartmentById(UUID id) {
        return departmentDAO.findById(id);
    }

    public Department updateDepartment(Department department) {
        return departmentDAO.update(department);
    }

    public boolean deleteDepartment(UUID id) {
        return departmentDAO.delete(id);
    }

    public List<Department> getDepartmentsByOrganization(UUID organizationId) {
        return departmentDAO.findByOrganization(organizationId);
    }
}
