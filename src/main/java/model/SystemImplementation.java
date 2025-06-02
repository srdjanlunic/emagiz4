package model;

import java.sql.Timestamp;
import java.util.UUID;

public class SystemImplementation {
    private UUID id;
    private UUID systemId;
    private UUID departmentId;
    private String dataClassification;
    private String criticalityLevel;
    private boolean internetFacing;
    private boolean sensitiveCustomerData;
    private int riskScore;
    private String version;
    private String environment;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public SystemImplementation() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.updatedAt = new Timestamp(System.currentTimeMillis());
        this.internetFacing = false;
        this.sensitiveCustomerData = false;
        this.riskScore = 0;
    }

    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getSystemId() { return systemId; }
    public void setSystemId(UUID systemId) { this.systemId = systemId; }

    public UUID getDepartmentId() { return departmentId; }
    public void setDepartmentId(UUID departmentId) { this.departmentId = departmentId; }

    public String getDataClassification() { return dataClassification; }
    public void setDataClassification(String dataClassification) { this.dataClassification = dataClassification; }

    public String getCriticalityLevel() { return criticalityLevel; }
    public void setCriticalityLevel(String criticalityLevel) { this.criticalityLevel = criticalityLevel; }

    public boolean isInternetFacing() { return internetFacing; }
    public void setInternetFacing(boolean internetFacing) { this.internetFacing = internetFacing; }

    public boolean isSensitiveCustomerData() { return sensitiveCustomerData; }
    public void setSensitiveCustomerData(boolean sensitiveCustomerData) { this.sensitiveCustomerData = sensitiveCustomerData; }

    public int getRiskScore() { return riskScore; }
    public void setRiskScore(int riskScore) { this.riskScore = riskScore; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
