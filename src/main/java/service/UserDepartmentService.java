package service;

import dao.UserDepartmentDAO;
import model.Department;
import model.User;

import java.util.List;
import java.util.UUID;

public class UserDepartmentService {
    private final UserDepartmentDAO dao = new UserDepartmentDAO();
    
    /**
     * Assigns a user to a department.
     *
     * @param userId the UUID of the user to assign
     * @param departmentId the UUID of the department to assign the user to
     */
    public void assignUserToDepartment(UUID userId, UUID departmentId) {
        dao.assign(userId, departmentId);
    }
    
    /**
     * Removes a user from a department.
     *
     * @param userId the UUID of the user to remove
     * @param departmentId the UUID of the department to remove the user from
     * @return true if the removal was successful, false otherwise
     */
    public boolean removeUserFromDepartment(UUID userId, UUID departmentId) {
        return dao.remove(userId, departmentId);
    }
    
    /**
     * Retrieves all departments assigned to a user.
     *
     * @param userId the UUID of the user
     * @return list of Department objects the user belongs to
     */
    public List<Department> getDepartmentsForUser(UUID userId) {
        return dao.findDepartmentsByUser(userId);
    }
    
    /**
     * Retrieves all users assigned to a department.
     *
     * @param departmentId the UUID of the department
     * @return list of User objects assigned to the department
     */
    public List<User> getUsersForDepartment(UUID departmentId) {
        return dao.findUsersByDepartment(departmentId);
    }
}
