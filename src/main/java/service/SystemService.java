package service;

import dao.SystemDAO;
import dao.SystemImplementationDAO;
import model.ITSystem;
import model.SystemImplementation;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public class SystemService {
    private SystemDAO systemDAO;
    private SystemImplementationDAO systemImplementationDAO;

    public SystemService() {
        this.systemDAO = new SystemDAO();
        this.systemImplementationDAO = new SystemImplementationDAO();
    }

    // Create new system
    public ITSystem createSystem(ITSystem system) {
        system.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        return systemDAO.create(system);
    }

    // Create new system implementation with risk calculation
    public SystemImplementation createSystemImplementation(SystemImplementation implementation) {
        // Calculate risk score based on system properties
        int riskScore = calculateRiskScore(implementation);
        implementation.setRiskScore(riskScore);
        implementation.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        implementation.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        return systemImplementationDAO.create(implementation);
    }

    // Calculate risk score based on system properties
    private int calculateRiskScore(SystemImplementation implementation) {
        int score = 0;

        // Internet facing adds risk
        if (implementation.isInternetFacing()) {
            score += 30;
        }

        // Data classification adds risk
        String dataClass = implementation.getDataClassification();
        if ("CONFIDENTIAL".equalsIgnoreCase(dataClass)) {
            score += 40;
        } else if ("SENSITIVE".equalsIgnoreCase(dataClass)) {
            score += 25;
        } else if ("INTERNAL".equalsIgnoreCase(dataClass)) {
            score += 10;
        }

        // Criticality level adds risk
        String criticality = implementation.getCriticalityLevel();
        if ("HIGH".equalsIgnoreCase(criticality)) {
            score += 30;
        } else if ("MEDIUM".equalsIgnoreCase(criticality)) {
            score += 20;
        } else if ("LOW".equalsIgnoreCase(criticality)) {
            score += 10;
        }

        // Sensitive customer data adds risk
        if (implementation.isSensitiveCustomerData()) {
            score += 20;
        }

        return Math.min(score, 100); // Cap at 100
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

    // System Implementation methods
    public List<SystemImplementation> getAllSystemImplementations() {
        return systemImplementationDAO.findAll();
    }

    public SystemImplementation getSystemImplementationById(UUID id) {
        return systemImplementationDAO.findById(id);
    }

    public SystemImplementation updateSystemImplementation(SystemImplementation implementation) {
        // Recalculate risk score on update
        int riskScore = calculateRiskScore(implementation);
        implementation.setRiskScore(riskScore);
        implementation.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        return systemImplementationDAO.update(implementation);
    }

    public boolean deleteSystemImplementation(UUID id) {
        return systemImplementationDAO.delete(id);
    }

    public List<SystemImplementation> getSystemImplementationsByDepartment(UUID departmentId) {
        return systemImplementationDAO.findByDepartment(departmentId);
    }

    public List<SystemImplementation> getSystemImplementationsBySystem(UUID systemId) {
        return systemImplementationDAO.findBySystem(systemId);
    }
}
