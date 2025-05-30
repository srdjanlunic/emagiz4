package dao;

import model.User;
import java.util.List;

public class UserDAO {

    // find user by username for login
    public User findByUsername(String username) {
        // TODO: implement database query
        return null;
    }

    // create new user
    public User create(User user) {
        // TODO: implement database insert
        return null;
    }

    // get user by id
    public User findById(Long id) {
        // TODO: implement database query
        return null;
    }

    // get all users
    public List<User> findAll() {
        // TODO: implement database query
        return null;
    }

    // update user
    public User update(User user) {
        // TODO: implement database update
        return null;
    }

    // delete user
    public boolean delete(Long id) {
        // TODO: implement database delete
        return false;
    }

    // find users by department
    public List<User> findByDepartment(Long departmentId) {
        // TODO: implement database query
        return null;
    }

    // find users by role
    public List<User> findByRole(String role) {
        // TODO: implement database query
        return null;
    }
}
