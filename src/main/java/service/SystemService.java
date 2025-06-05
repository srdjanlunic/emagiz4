package service;

import dao.SystemDAO;
import model.ITSystem;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public class SystemService {
    private SystemDAO systemDAO;

    public SystemService() {
        this.systemDAO = new SystemDAO();
    }

    public ITSystem createSystem(ITSystem system) {
        try {
            system.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            return systemDAO.create(system);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<ITSystem> getAllSystems() {
        return systemDAO.findAll();
    }

    public ITSystem getSystemById(UUID id) {
        return systemDAO.findById(id);
    }

    public ITSystem updateSystem(ITSystem system) {
        return systemDAO.update(system);
    }

    public boolean deleteSystem(UUID id) {
        return systemDAO.delete(id);
    }
}
