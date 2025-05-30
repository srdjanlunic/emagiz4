package service;

import dao.SystemDAO;
import model.ITSystem;
import java.util.List;

public class SystemService {
    private SystemDAO systemDAO;

    public SystemService() {
        this.systemDAO = new SystemDAO();
    }

    // register new system (security officer only)
    public ITSystem registerSystem(ITSystem system) {
        // TODO: validate system data
        // TODO: calculate risk score
        // TODO: save to database
        return null;
    }

    // get system by id
    public ITSystem getSystemById(Long id) {
        // TODO: get system from database
        return null;
    }

    // get all systems
    public List<ITSystem> getAllSystems() {
        // TODO: get all systems from database
        return null;
    }

    // update system
    public ITSystem updateSystem(ITSystem system) {
        // TODO: validate system data
        // TODO: recalculate risk score
        // TODO: update in database
        return null;
    }

    // delete system
    public boolean deleteSystem(Long id) {
        // TODO: delete system from database
        return false;
    }

    // get systems by owner
    public List<ITSystem> getSystemsByOwner(Long ownerId) {
        // TODO: get systems from database
        return null;
    }

    // calculate risk score for system
    public int calculateRiskScore(ITSystem system) {
        // TODO: implement risk calculation logic
        return 0;
    }
}
