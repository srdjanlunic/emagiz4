package service;

import dao.RoleDAO;
import dao.UserDAO;
import model.User;
import model.Role;
import security.JWTUtil;
import org.mindrot.jbcrypt.BCrypt;
import java.util.UUID;

/**
 * Service for user authentication and authorization.
 * Handles user login, password hashing, role lookup, and JWT token management.
 */
public class AuthService {
    private UserDAO userDAO;
    private RoleDAO roleDAO;
    
    public AuthService() {
        this.userDAO = new UserDAO();
        this.roleDAO = new RoleDAO();
    }
    
    /**
     * Validates a username to prevent injection or XSS.
     * Only allows letters, digits, dot, underscore, and dash.
     *
     * @param username the username to validate
     * @return true if valid; false otherwise
     */
    private boolean isValidUsername(String username) {
        if (username == null || username.isBlank()) return false;
        // Regex whitelist: allow a-z, A-Z, 0-9, dot, underscore, dash
        return username.matches("[a-zA-Z0-9._-]+");
    }
    
    /**
     * Validates a password to ensure it is not null or blank.
     * (No special character restrictions applied here;
     *  more complex password policies can be implemented as needed.)
     *
     * @param password the password to validate
     * @return true if valid; false otherwise
     */
    private boolean isValidPassword(String password) {
        return password != null && !password.isBlank();
    }
    
    
    
    /**
     * Authenticates a user by username and password.
     *
     * @param username the username of the user attempting to authenticate
     * @param password the plaintext password provided by the user
     * @return the authenticated User object if credentials are valid; otherwise, null
     */
    public User authenticate(String username, String password) {
        if(!isValidUsername(username) || !isValidPassword(password)) {
            return null;
        }
        User user = userDAO.findByUsername(username);
        if (user != null && verifyPassword(password, user.getPassword())) {
            return user;
        }
        return null;
    }
    
    /**
     * Retrieves the Role object associated with a given user.
     *
     * @param user the User whose role is to be retrieved
     * @return the Role object for the user, or null if user or role ID is null
     */
    public Role getRoleByUser(User user) {
        if (user == null || user.getRoleId() == null) {
            return null;
        }
        return roleDAO.findById(user.getRoleId());
    }
    
    /**
     * Finds a user by role name, useful for demo login purposes.
     *
     * @param roleName the name of the role to search for
     * @return the first User found with the given role, or null if none found
     */
    public User findUserByRole(String roleName) {
        Role role = roleDAO.findByName(roleName);
        if (role != null) {
            return userDAO.findFirstByRoleId(role.getId());
        }
        return null;
    }
    
    /**
     * Hashes a plaintext password using BCrypt for secure storage.
     *
     * @param password the plaintext password to hash
     * @return the BCrypt hashed password string
     */
    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
    
    /**
     * Verifies a plaintext password against a stored BCrypt hash.
     *
     * @param password   the plaintext password to verify
     * @param storedHash the BCrypt hashed password stored in the database
     * @return true if the password matches the stored hash, false otherwise
     */
    public boolean verifyPassword(String password, String storedHash) {
        if (password == null || storedHash == null) {
            return false;
        }
        return BCrypt.checkpw(password, storedHash);
    }
    
    /**
     * Stub for verifying simple Base64 encoded passwords (not implemented).
     *
     * @param password       the plaintext password to verify
     * @param storedPassword the stored Base64 encoded password
     * @return always returns false as method is not implemented
     */
    public boolean verifySimplePassword(String password, String storedPassword) {
        return false;
    }
    
    /**
     * Stub for verifying legacy "hashed-" prefix passwords (not implemented).
     *
     * @param password       the plaintext password to verify
     * @param storedPassword the stored legacy hashed password
     * @return always returns false as method is not implemented
     */
    public boolean verifyLegacyPassword(String password, String storedPassword) {
        return false;
    }
    
    /**
     * Generates a JWT token for the specified user.
     *
     * @param user the User for whom to generate the token
     * @return a JWT token string
     */
    public String generateToken(User user) {
        return JWTUtil.generateToken(user);
    }
    
    /**
     * Validates a JWT token and returns the corresponding User if valid.
     *
     * @param token the JWT token string to validate
     * @return the User associated with the token if valid, otherwise null
     */
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
