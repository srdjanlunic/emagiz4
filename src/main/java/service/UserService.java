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

    public User getUserById(UUID id) {
        return userDAO.findById(id);
    }

    public List<UserDto> getAllUsers() {
        return userDAO.findAll();
    }

    public User updateUser(User user) {
        if (validateUserData(user)) {
            return userDAO.update(user);
        }
        return null;
    }

    public boolean deleteUser(UUID id) {
        return userDAO.delete(id);
    }

    public List<User> getUsersByDepartment(UUID departmentId) {
        return userDAO.findByDepartment(departmentId);
    }

    public List<User> getUsersByRole(UUID roleId) {
        return userDAO.findByRole(roleId);
    }

    public User getUserByUsername(String username) {
        return userDAO.findByUsername(username);
    }

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
