package service;

import dao.RoleDAO;
import java.util.UUID;
import model.Role;

public class RoleService {

    private RoleDAO roleDAO;

    public RoleService() {
        this.roleDAO = new RoleDAO();
    }

    public Role getRoleByName(String roleName) {
        return roleDAO.findByName(roleName);
    }
    public Role getRoleById(UUID roleId) {
        return roleDAO.findById(roleId);
    }
} 