package service;

import dao.UserDAO;
import model.User;
import java.util.List;

public class UserService {
    private UserDAO userDAO;
    private AuthService authService;

    public UserService() {
        this.userDAO = new UserDAO();
        this.authService = new AuthService();
    }

    // create new user (admin only)
    public User createUser(User user) {
        // TODO: hash password before saving
        // TODO: validate user data
        // TODO: save to database
        return null;
    }

    // get user by id
    public User getUserById(Long id) {
        // TODO: get user from database
        return null;
    }

    // get all users
    public List<User> getAllUsers() {
        // TODO: get all users from database
        return null;
    }

    // update user
    public User updateUser(User user) {
        // TODO: validate user data
        // TODO: update in database
        return null;
    }

    // delete user (admin only)
    public boolean deleteUser(Long id) {
        // TODO: delete user from database
        return false;
    }

    // get users by department
    public List<User> getUsersByDepartment(Long departmentId) {
        // TODO: get users from database
        return null;
    }
}
