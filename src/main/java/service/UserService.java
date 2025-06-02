package service;

import dao.UserDAO;
import model.User;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public class UserService {
    private UserDAO userDAO;
    private AuthService authService;

    public UserService() {
        this.userDAO = new UserDAO();
        this.authService = new AuthService();
    }

    // create new user (admin only)
    public User createUser(User user) {
        // Hash password before saving
        if (user.getPassword() != null) {
            String hashedPassword = authService.hashPassword(user.getPassword());
            user.setPassword(hashedPassword);
        }

        // Set creation timestamp
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        // Validate user data
        if (validateUserData(user)) {
            return userDAO.create(user);
        }
        return null;
    }

    // get user by id
    public User getUserById(UUID id) {
        return userDAO.findById(id);
    }

    // get all users
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    // update user
    public User updateUser(User user) {
        // Validate user data
        if (validateUserData(user)) {
            return userDAO.update(user);
        }
        return null;
    }

    // delete user (admin only)
    public boolean deleteUser(UUID id) {
        return userDAO.delete(id);
    }

    // get users by department
    public List<User> getUsersByDepartment(UUID departmentId) {
        return userDAO.findByDepartment(departmentId);
    }

    // get users by role
    public List<User> getUsersByRole(UUID roleId) {
        return userDAO.findByRole(roleId);
    }

    // get user by username
    public User getUserByUsername(String username) {
        return userDAO.findByUsername(username);
    }

    // validate user data
    private boolean validateUserData(User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return false;
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            return false;
        }
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            return false;
        }
        if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
            return false;
        }
        return true;
    }
}
