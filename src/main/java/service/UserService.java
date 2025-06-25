package service;

import dao.UserDAO;
import model.User;
import model.Role;
import dto.UserDto;
import dto.UserCreationRequestDto;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public class UserService {
    private UserDAO userDAO;
    private AuthService authService;
    private RoleService roleService;
    
    public UserService() {
        this.userDAO = new UserDAO();
        this.authService = new AuthService();
        this.roleService = new RoleService();
    }
    
    /**
     * Creates a new user from the given request data.
     * If password is missing, generates a temporary password.
     * Assigns the role by name or defaults to "SYSTEM_OWNER".
     *
     * @param userRequest the user creation request DTO
     * @return the created User, or null if creation failed or data invalid
     */
    public User createUser(UserCreationRequestDto userRequest) {
        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        
        if (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty()) {
            user.setPassword(authService.hashPassword(userRequest.getPassword()));
        } else {
            String tempPassword = UUID.randomUUID().toString().substring(0, 8);
            user.setPassword(authService.hashPassword(tempPassword));
        }
        
        Role role = roleService.getRoleByName(userRequest.getRole());
        if (role != null) {
            user.setRoleId(role.getId());
        } else {
            Role defaultRole = roleService.getRoleByName("SYSTEM_OWNER");
            if (defaultRole != null) {
                user.setRoleId(defaultRole.getId());
            } else {
                System.err.println("Default role 'SYSTEM_OWNER' not found.");
                return null;
            }
        }
        
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        user.setFirstName("");
        user.setLastName("");
        
        if (validateUserData(user)) {
            return userDAO.create(user);
        }
        return null;
    }
    
    /**
     * Retrieves a user by their unique ID.
     *
     * @param id the UUID of the user
     * @return the User if found, or null otherwise
     */
    public User getUserById(UUID id) {
        return userDAO.findById(id);
    }
    
    /**
     * Retrieves all users as UserDto list.
     *
     * @return list of UserDto representing all users
     */
    public List<UserDto> getAllUsers() {
        return userDAO.findAll();
    }
    
    /**
     * Updates the given user if valid.
     *
     * @param user the User entity with updated data
     * @return the updated User, or null if validation fails
     */
    public User updateUser(User user) {
        if (validateUserData(user)) {
            return userDAO.update(user);
        }
        return null;
    }
    
    /**
     * Deletes a user by their ID.
     *
     * @param id the UUID of the user to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteUser(UUID id) {
        return userDAO.delete(id);
    }
    
    /**
     * Retrieves all users assigned to a specific department.
     *
     * @param departmentId the UUID of the department
     * @return list of Users assigned to the department
     */
    public List<User> getUsersByDepartment(UUID departmentId) {
        return userDAO.findByDepartment(departmentId);
    }
    
    /**
     * Retrieves all users with a specific role.
     *
     * @param roleId the UUID of the role
     * @return list of Users with the given role
     */
    public List<User> getUsersByRole(UUID roleId) {
        return userDAO.findByRole(roleId);
    }
    
    /**
     * Retrieves a user by username.
     *
     * @param username the username string
     * @return the User if found, or null otherwise
     */
    public User getUserByUsername(String username) {
        return userDAO.findByUsername(username);
    }
    
    /**
     * Validates essential user data fields.
     *
     * @param user the User entity to validate
     * @return true if valid, false if username or email are missing or empty
     */
    private boolean validateUserData(User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return false;
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            return false;
        }
        return true;
    }
}
