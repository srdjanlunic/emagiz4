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

public class AuthService {
    private UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    // Authenticate user with username and password
    public User authenticate(String username, String password) {
        User user = userDAO.findByUsername(username);
        if (user == null || user.getPassword() == null) {
            return null;
        }
        
        // Check all possible password formats
        if (verifyPassword(password, user.getPassword()) || 
            verifySimplePassword(password, user.getPassword()) || 
            verifyLegacyPassword(password, user.getPassword())) {
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
        try {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes("UTF-8"));
            byte[] combined = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hashedPassword, 0, combined, salt.length, hashedPassword.length);
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    // Verify password against salted hash
    public boolean verifyPassword(String password, String storedHash) {
        if (storedHash == null) return false;
        try {
            byte[] combined = Base64.getDecoder().decode(storedHash);
            if (combined.length < 16) return false;
            byte[] salt = new byte[16];
            System.arraycopy(combined, 0, salt, 0, 16);
            byte[] storedPasswordHash = new byte[combined.length - 16];
            System.arraycopy(combined, 16, storedPasswordHash, 0, storedPasswordHash.length);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] providedPasswordHash = md.digest(password.getBytes("UTF-8"));
            return MessageDigest.isEqual(storedPasswordHash, providedPasswordHash);
        } catch (Exception e) {
            return false;
        }
    }

    // Verify simple Base64 encoded passwords
    public boolean verifySimplePassword(String password, String storedPassword) {
        if (storedPassword == null) return false;
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(storedPassword);
            String decodedPassword = new String(decodedBytes, "UTF-8");
            return password.equals(decodedPassword);
        } catch (Exception e) {
            return false;
        }
    }

    // Verify legacy "hashed-" prefix passwords
    public boolean verifyLegacyPassword(String password, String storedPassword) {
        if (storedPassword == null) return false;
        return storedPassword.equals("hashed-" + password);
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
