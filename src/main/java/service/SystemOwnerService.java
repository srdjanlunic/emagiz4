package service;

import dao.SystemOwnerDAO;
import model.SystemImplementation;
import model.User;

import java.util.List;
import java.util.UUID;

public class SystemOwnerService {
    private final SystemOwnerDAO dao = new SystemOwnerDAO();
    
    /**
     * Assigns a user as the owner of a system implementation.
     *
     * @param userId the UUID of the user to assign as owner
     * @param implId the UUID of the system implementation
     */
    public void assignOwner(UUID userId, UUID implId) {
        dao.assignOwner(userId, implId);
    }
    
    /**
     * Removes an ownership assignment between a user and a system implementation.
     *
     * @param userId the UUID of the user
     * @param implId the UUID of the system implementation
     * @return true if the ownership was removed; false if no such assignment existed
     */
    public boolean removeOwner(UUID userId, UUID implId) {
        return dao.removeOwner(userId, implId);
    }
    
    /**
     * Retrieves the list of users who own a given system implementation.
     *
     * @param implId the UUID of the system implementation
     * @return list of User objects assigned as owners
     */
    public List<User> getOwnersForImplementation(UUID implId) {
        return dao.findOwnersByImplementation(implId);
    }
    
    /**
     * Retrieves the list of system implementations owned by a given user.
     *
     * @param userId the UUID of the user
     * @return list of SystemImplementation objects owned by the user
     */
    public List<SystemImplementation> getImplementationsForOwner(UUID userId) {
        return dao.findImplementationsByOwner(userId);
    }
}
