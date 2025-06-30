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
        System.out.println("AuthService.authenticate called with username: " + username);
        
        if(!isValidUsername(username) || !isValidPassword(password)) {
            System.out.println("Invalid username or password format");
            return null;
        }
        
        User user = userDAO.findByUsername(username);
        System.out.println("User found: " + (user != null ? user.getUsername() : "null"));
        
        if (user != null && user.getPassword() != null) {
            System.out.println("Comparing passwords - provided: " + password + ", stored: " + user.getPassword());
            if (user.getPassword().equals(password)) {
                System.out.println("Password match successful");
                return user;
            } else {
                System.out.println("Password mismatch");
            }
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
            System.out.println("User or roleId is null");
            return null;
        }
        System.out.println("Looking up role with ID: " + user.getRoleId());
        Role role = roleDAO.findById(user.getRoleId());
        System.out.println("Role found: " + (role != null ? role.getName() : "null"));
        return role;
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
