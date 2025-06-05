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

    // Create new system implementation with risk calculation
    public SystemImplementation createSystemImplementation(SystemImplementation implementation) {
        System.out.println("=== SystemImplementationService.createSystemImplementation called ===");
        System.out.println("Input implementation - SystemID: " + implementation.getSystemId());
        System.out.println("Input implementation - DepartmentID: " + implementation.getDepartmentId());

        try {
            // Calculate risk score based on system properties
            int riskScore = calculateRiskScore(implementation);
            implementation.setRiskScore(riskScore);
            System.out.println("Calculated risk score: " + riskScore);

            // Set timestamps
            implementation.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            implementation.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            System.out.println("Set timestamps");

            System.out.println("About to call systemImplementationDAO.create...");
            SystemImplementation result = systemImplementationDAO.create(implementation);
            System.out.println("systemImplementationDAO.create returned: " + (result != null ? "SUCCESS with ID " + result.getId() : "NULL"));
            return result;
        } catch (Exception e) {
            System.out.println("❌ Exception in SystemImplementationService.createSystemImplementation: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
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
