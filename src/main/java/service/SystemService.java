package service;

import dao.SystemDAO;
import model.ITSystem;
import java.sql.Timestamp;
import java.util.List;

public class SystemService {
    private SystemDAO systemDAO;

    public SystemService() {
        this.systemDAO = new SystemDAO();
    }

    // Create new system with risk calculation
    public ITSystem createSystem(ITSystem system) {
        // Calculate risk score based on system properties
        int riskScore = calculateRiskScore(system);
        system.setRiskScore(riskScore);
        system.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        return systemDAO.create(system);
    }

    // Calculate risk score based on system properties
    private int calculateRiskScore(ITSystem system) {
        int score = 0;

        // Internet facing adds risk
        if (system.isInternetFacing()) {
            score += 30;
        }

        // Data classification adds risk
        String dataClass = system.getDataClassification();
        if ("CONFIDENTIAL".equalsIgnoreCase(dataClass)) {
            score += 40;
        } else if ("SENSITIVE".equalsIgnoreCase(dataClass)) {
            score += 25;
        } else if ("INTERNAL".equalsIgnoreCase(dataClass)) {
            score += 10;
        }

        // Criticality level adds risk
        String criticality = system.getCriticalityLevel();
        if ("HIGH".equalsIgnoreCase(criticality)) {
            score += 30;
        } else if ("MEDIUM".equalsIgnoreCase(criticality)) {
            score += 20;
        } else if ("LOW".equalsIgnoreCase(criticality)) {
            score += 10;
        }

        return Math.min(score, 100); // Cap at 100
    }

    public List<ITSystem> getAllSystems() {
        return systemDAO.findAll();
    }

    public ITSystem getSystemById(Long id) {
        return systemDAO.findById(id);
    }

    public ITSystem updateSystem(ITSystem system) {
        // Recalculate risk score on update
        int riskScore = calculateRiskScore(system);
        system.setRiskScore(riskScore);

        return systemDAO.update(system);
    }

    public boolean deleteSystem(Long id) {
        return systemDAO.delete(id);
    }

    public List<ITSystem> getSystemsByOwner(Long ownerId) {
        return systemDAO.findByOwner(ownerId);
    }

    public List<ITSystem> getSystemsByDepartment(Long departmentId) {
        return systemDAO.findByDepartment(departmentId);
    }
}
