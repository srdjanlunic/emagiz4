package service;

import dao.UserDAO;
import model.User;

public class AuthService {
    private UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    // login user with username and password
    public User login(String username, String password) {
        // TODO: find user by username
        // TODO: verify password hash
        // TODO: return user if valid, null if invalid
        return null;
    }

    // hash password for storage
    public String hashPassword(String password) {
        // TODO: implement password hashing
        return null;
    }

    // verify password against hash
    public boolean verifyPassword(String password, String hash) {
        // TODO: implement password verification
        return false;
    }

    // generate JWT token for user
    public String generateToken(User user) {
        // TODO: implement JWT token generation
        return null;
    }

    // validate JWT token
    public User validateToken(String token) {
        // TODO: implement JWT token validation
        // TODO: return user if valid, null if invalid
        return null;
    }
}
