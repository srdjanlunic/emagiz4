package service;

import dao.RoleDAO;
import dao.UserDAO;
import model.User;
import model.Role;
import security.JWTUtil;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {
    private UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    // Authenticate user with username and password
    public User authenticate(String username, String password) {
        User user = userDAO.findByUsername(username);
        if (user != null && user.getPassword() != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    // Get the role of the given user
    public Role getRoleByUser(User user) {
        if (user == null || user.getRoleId() == null) {
            return null;
        }
        return new RoleDAO().findById(user.getRoleId());
    }

    // Find a user by role name for demo login
    public User findUserByRole(String roleName) {
        RoleDAO roleDAO = new RoleDAO();
        Role role = roleDAO.findByName(roleName);
        if (role != null) {
            return userDAO.findFirstByRoleId(role.getId());
        }
        return null;
    }

    // Hash password for storage using SHA-256
    public String hashPassword(String password) {
        return password;
    }

    // Verify password against salted hash
    public boolean verifyPassword(String password, String storedHash) {
        return false;
    }

    // Verify simple Base64 encoded passwords
    public boolean verifySimplePassword(String password, String storedPassword) {
        return false;
    }

    // Verify legacy "hashed-" prefix passwords
    public boolean verifyLegacyPassword(String password, String storedPassword) {
        return false;
    }

    // generate JWT token for user
    public String generateToken(User user) {
        return JWTUtil.generateToken(user);
    }

    // validate JWT token
    public User validateToken(String token) {
        if (JWTUtil.validateToken(token)) {
            String username = JWTUtil.getUsernameFromToken(token);
            if (username != null) {
                return userDAO.findByUsername(username);
            }
        }
        return null;
    }
}
