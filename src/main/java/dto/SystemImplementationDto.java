package dto;

import java.util.UUID;
import java.sql.Timestamp;

/**
 * Data Transfer Object for system implementation details.
 */
public class SystemImplementationDto {
    private UUID id;
    private UUID systemId;
    private UUID departmentId;
    private String dataClassification;
    private String criticalityLevel;
    private boolean internetFacing;
    private boolean sensitiveCustomerData;
    private String riskScore;
    private String version;
    private String environment;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    /**
     * Constructs a SystemImplementationDto with all fields.
     *
     * @param id                    Unique ID of the implementation
     * @param systemId              ID of the related system
     * @param departmentId          ID of the owning department
     * @param dataClassification    Data classification level (e.g., confidential, public)
     * @param criticalityLevel      Criticality level (e.g., high, medium)
     * @param internetFacing        True if exposed to the internet
     * @param sensitiveCustomerData True if it handles sensitive customer data
     * @param riskScore             Risk score assigned
     * @param version               Version of the implementation
     * @param environment           Environment type (e.g., production, test)
     * @param createdAt             Creation timestamp
     * @param updatedAt             Last updated timestamp
     */
    public SystemImplementationDto(UUID id, UUID systemId, UUID departmentId, String dataClassification, String criticalityLevel,
                                   boolean internetFacing, boolean sensitiveCustomerData, String riskScore,
                                   String version, String environment, Timestamp createdAt, Timestamp updatedAt) {
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
    
    /** @return Implementation ID */
    public UUID getId() {
        return id;
    }
    
    /** @param id Implementation ID */
    public void setId(UUID id) {
        this.id = id;
    }
    
    /** @return Related system ID */
    public UUID getSystemId() {
        return systemId;
    }
    
    /** @param systemId Related system ID */
    public void setSystemId(UUID systemId) {
        this.systemId = systemId;
    }
    
    /** @return Owning department ID */
    public UUID getDepartmentId() {
        return departmentId;
    }
    
    /** @param departmentId Owning department ID */
    public void setDepartmentId(UUID departmentId) {
        this.departmentId = departmentId;
    }
    
    /** @return Data classification level */
    public String getDataClassification() {
        return dataClassification;
    }
    
    /** @param dataClassification Data classification level */
    public void setDataClassification(String dataClassification) {
        this.dataClassification = dataClassification;
    }
    
    /** @return Criticality level */
    public String getCriticalityLevel() {
        return criticalityLevel;
    }
    
    /** @param criticalityLevel Criticality level */
    public void setCriticalityLevel(String criticalityLevel) {
        this.criticalityLevel = criticalityLevel;
    }
    
    /** @return True if system is internet-facing */
    public boolean isInternetFacing() {
        return internetFacing;
    }
    
    /** @param internetFacing Set if system is internet-facing */
    public void setInternetFacing(boolean internetFacing) {
        this.internetFacing = internetFacing;
    }
    
    /** @return True if system handles sensitive customer data */
    public boolean isSensitiveCustomerData() {
        return sensitiveCustomerData;
    }
    
    /** @param sensitiveCustomerData Set if system handles sensitive customer data */
    public void setSensitiveCustomerData(boolean sensitiveCustomerData) {
        this.sensitiveCustomerData = sensitiveCustomerData;
    }
    
    /** @return Risk score */
    public String getRiskScore() {
        return riskScore;
    }
    
    /** @param riskScore Risk score */
    public void setRiskScore(String riskScore) {
        this.riskScore = riskScore;
    }
    
    /** @return Implementation version */
    public String getVersion() {
        return version;
    }
    
    /** @param version Implementation version */
    public void setVersion(String version) {
        this.version = version;
    }
    
    /** @return Environment (e.g., production, test) */
    public String getEnvironment() {
        return environment;
    }
    
    /** @param environment Environment (e.g., production, test) */
    public void setEnvironment(String environment) {
        this.environment = environment;
    }
    
    /** @return Creation timestamp */
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    /** @param createdAt Creation timestamp */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    /** @return Last updated timestamp */
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
    
    /** @param updatedAt Last updated timestamp */
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
