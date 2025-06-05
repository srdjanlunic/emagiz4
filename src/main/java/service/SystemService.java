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

    // Create new system
    public ITSystem createSystem(ITSystem system) {
        System.out.println("=== SystemService.createSystem called ===");
        System.out.println("Input system: " + system.getName());

        try {
            system.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            System.out.println("About to call systemDAO.create...");
            ITSystem result = systemDAO.create(system);
            System.out.println("systemDAO.create returned: " + (result != null ? "SUCCESS with ID " + result.getId() : "NULL"));
            return result;
        } catch (Exception e) {
            System.out.println("❌ Exception in SystemService.createSystem: " + e.getMessage());
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
