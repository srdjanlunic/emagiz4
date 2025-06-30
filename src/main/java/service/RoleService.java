package service;

import dao.RoleDAO;
import java.util.UUID;
import model.Role;

/**
 * Service class to manage roles.
 */
public class RoleService {
    
    private RoleDAO roleDAO;
    
    /**
     * Constructs a new RoleService with a RoleDAO instance.
     */
    public RoleService() {
        this.roleDAO = new RoleDAO();
    }
    
    /**
     * Finds a role by its name.
     *
     * @param roleName the name of the role to find
     * @return the Role object if found, otherwise null
     */
    public Role getRoleByName(String roleName) {
        return roleDAO.findByName(roleName);
    }
    
    /**
     * Retrieves a role by its unique identifier.
     *
     * @param id the role ID
     * @return the role if found, null otherwise
     */
    public Role getRoleById(UUID id) {
        return roleDAO.findById(id);
    }
}
