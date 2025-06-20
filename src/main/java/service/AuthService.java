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
    private RoleDAO roleDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
        this.roleDAO = new RoleDAO();
    }

    // Authenticate user with username and password
    public User authenticate(String username, String password) {
        User user = userDAO.findByUsername(username);
        if (user != null && verifyPassword(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    // Get the role of the given user
    public Role getRoleByUser(User user) {
        if (user == null || user.getRoleId() == null) {
            return null;
        }
        return roleDAO.findById(user.getRoleId());
    }

    // Find a user by role name for demo login
    public User findUserByRole(String roleName) {
        Role role = roleDAO.findByName(roleName);
        if (role != null) {
            return userDAO.findFirstByRoleId(role.getId());
        }
        return null;
    }

    // Hash password for storage using BCrypt
    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    // Verify password against stored hash
    public boolean verifyPassword(String password, String storedHash) {
        if (password == null || storedHash == null) {
            return false;
        }
        return BCrypt.checkpw(password, storedHash);
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
