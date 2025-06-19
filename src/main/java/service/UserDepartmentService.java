// service/UserDepartmentService.java
package service;

import dao.UserDepartmentDAO;
import model.Department;
import model.User;

import java.util.List;
import java.util.UUID;

public class UserDepartmentService {
    private final UserDepartmentDAO dao = new UserDepartmentDAO();

    public void assignUserToDepartment(UUID userId, UUID departmentId) {
        dao.assign(userId, departmentId);
    }

    public boolean removeUserFromDepartment(UUID userId, UUID departmentId) {
        return dao.remove(userId, departmentId);
    }

    public List<Department> getDepartmentsForUser(UUID userId) {
        return dao.findDepartmentsByUser(userId);
    }

    public List<User> getUsersForDepartment(UUID departmentId) {
        return dao.findUsersByDepartment(departmentId);
    }
}
