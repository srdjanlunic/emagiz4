// service/SystemOwnerService.java
package service;

import dao.SystemOwnerDAO;
import model.SystemImplementation;
import model.User;

import java.util.List;
import java.util.UUID;

public class SystemOwnerService {
    private final SystemOwnerDAO dao = new SystemOwnerDAO();

    public void assignOwner(UUID userId, UUID implId) {
        dao.assignOwner(userId, implId);
    }

    public boolean removeOwner(UUID userId, UUID implId) {
        return dao.removeOwner(userId, implId);
    }

    public List<User> getOwnersForImplementation(UUID implId) {
        return dao.findOwnersByImplementation(implId);
    }

    public List<SystemImplementation> getImplementationsForOwner(UUID userId) {
        return dao.findImplementationsByOwner(userId);
    }
}

