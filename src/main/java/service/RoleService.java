package service;

import dao.RoleDAO;
import model.Role;

public class RoleService {

    private RoleDAO roleDAO;

    public RoleService() {
        this.roleDAO = new RoleDAO();
    }

    public Role getRoleByName(String roleName) {
        return roleDAO.findByName(roleName);
    }
} 