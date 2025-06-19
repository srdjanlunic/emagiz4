package dto;

import java.util.UUID;
import java.sql.Timestamp;

public class SystemImplementationDto {
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

    // Constructors, Getters and Setters
    public SystemImplementationDto(UUID id, UUID systemId, UUID departmentId, String dataClassification, String criticalityLevel, boolean internetFacing, boolean sensitiveCustomerData, int riskScore, String version, String environment, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.systemId = systemId;
        this.departmentId = departmentId;
        this.dataClassification = dataClassification;
        this.criticalityLevel = criticalityLevel;
        this.internetFacing = internetFacing;
        this.sensitiveCustomerData = sensitiveCustomerData;
        this.riskScore = riskScore;
        this.version = version;
        this.environment = environment;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getSystemId() {
        return systemId;
    }

    public void setSystemId(UUID systemId) {
        this.systemId = systemId;
    }

    public UUID getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(UUID departmentId) {
        this.departmentId = departmentId;
    }

    public String getDataClassification() {
        return dataClassification;
    }

    public void setDataClassification(String dataClassification) {
        this.dataClassification = dataClassification;
    }

    public String getCriticalityLevel() {
        return criticalityLevel;
    }

    public void setCriticalityLevel(String criticalityLevel) {
        this.criticalityLevel = criticalityLevel;
    }

    public boolean isInternetFacing() {
        return internetFacing;
    }

    public void setInternetFacing(boolean internetFacing) {
        this.internetFacing = internetFacing;
    }

    public boolean isSensitiveCustomerData() {
        return sensitiveCustomerData;
    }

    public void setSensitiveCustomerData(boolean sensitiveCustomerData) {
        this.sensitiveCustomerData = sensitiveCustomerData;
    }

    public int getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(int riskScore) {
        this.riskScore = riskScore;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
} 