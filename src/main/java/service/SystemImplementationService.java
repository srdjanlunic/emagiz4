package service;

import dao.SystemImplementationDAO;
import model.SystemImplementation;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public class SystemImplementationService {
    private SystemImplementationDAO systemImplementationDAO;

    public SystemImplementationService() {
        this.systemImplementationDAO = new SystemImplementationDAO();
    }

    public SystemImplementation createSystemImplementation(SystemImplementation implementation) {
        try {
            int riskScore = calculateRiskScore(implementation);
            implementation.setRiskScore(riskScore);
            implementation.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            implementation.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

            return systemImplementationDAO.create(implementation);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private int calculateRiskScore(SystemImplementation implementation) {
        int score = 0;

        if (implementation.isInternetFacing()) {
            score += 30;
        }

        String dataClass = implementation.getDataClassification();
        if ("CONFIDENTIAL".equalsIgnoreCase(dataClass)) {
            score += 40;
        } else if ("SENSITIVE".equalsIgnoreCase(dataClass)) {
            score += 25;
        } else if ("INTERNAL".equalsIgnoreCase(dataClass)) {
            score += 10;
        }

        String criticality = implementation.getCriticalityLevel();
        if ("HIGH".equalsIgnoreCase(criticality)) {
            score += 30;
        } else if ("MEDIUM".equalsIgnoreCase(criticality)) {
            score += 20;
        } else if ("LOW".equalsIgnoreCase(criticality)) {
            score += 10;
        }

        if (implementation.isSensitiveCustomerData()) {
            score += 20;
        }

        return Math.min(score, 100);
    }

    public List<SystemImplementation> getAllSystemImplementations() {
        return systemImplementationDAO.findAll();
    }

    public SystemImplementation getSystemImplementationById(UUID id) {
        return systemImplementationDAO.findById(id);
    }

    public SystemImplementation updateSystemImplementation(SystemImplementation implementation) {
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
