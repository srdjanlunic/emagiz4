package service;

import dao.UserDAO;
import model.User;
import security.JWTUtil;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class AuthService {
    private UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    // authenticate user with username and password
    public User authenticate(String username, String password) {
        User user = userDAO.findByUsername(username);
        if (user != null && verifyPassword(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    // login user with username and password (alias for authenticate)
    public User login(String username, String password) {
        return authenticate(username, password);
    }

    // hash password for storage using SHA-256
    public String hashPassword(String password) {
        try {
            // Generate salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);

            // Hash password with salt
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes("UTF-8"));

            // Combine salt and hash
            byte[] combined = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hashedPassword, 0, combined, salt.length, hashedPassword.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    // verify password against hash
    public boolean verifyPassword(String password, String storedHash) {
        try {
            byte[] combined = Base64.getDecoder().decode(storedHash);

            // Extract salt (first 16 bytes)
            byte[] salt = new byte[16];
            System.arraycopy(combined, 0, salt, 0, 16);

            // Extract stored hash (remaining bytes)
            byte[] storedPasswordHash = new byte[combined.length - 16];
            System.arraycopy(combined, 16, storedPasswordHash, 0, storedPasswordHash.length);

            // Hash the provided password with the same salt
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] providedPasswordHash = md.digest(password.getBytes("UTF-8"));

            // Compare hashes
            return MessageDigest.isEqual(storedPasswordHash, providedPasswordHash);
        } catch (Exception e) {
            return false;
        }
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
