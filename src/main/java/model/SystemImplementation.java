package model;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * Represents a system implementation within a department, including its classification and risk info.
 */
public class SystemImplementation {
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
     * Default constructor initializes timestamps and booleans with default values.
     */
    public SystemImplementation() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.updatedAt = new Timestamp(System.currentTimeMillis());
        this.internetFacing = false;
        this.sensitiveCustomerData = false;
        this.riskScore = null;
    }
    
    /**
     * Gets the unique ID of this system implementation.
     * @return UUID of the implementation
     */
    public UUID getId() { return id; }
    
    /**
     * Sets the unique ID of this system implementation.
     * @param id UUID to set
     */
    public void setId(UUID id) { this.id = id; }
    
    /**
     * Gets the ID of the system this implementation belongs to.
     * @return System UUID
     */
    public UUID getSystemId() { return systemId; }
    
    /**
     * Sets the ID of the system this implementation belongs to.
     * @param systemId System UUID to set
     */
    public void setSystemId(UUID systemId) { this.systemId = systemId; }
    
    /**
     * Gets the ID of the department where the system is implemented.
     * @return Department UUID
     */
    public UUID getDepartmentId() { return departmentId; }
    
    /**
     * Sets the ID of the department where the system is implemented.
     * @param departmentId Department UUID to set
     */
    public void setDepartmentId(UUID departmentId) { this.departmentId = departmentId; }
    
    /**
     * Gets the data classification level of this implementation.
     * @return Data classification string
     */
    public String getDataClassification() { return dataClassification; }
    
    /**
     * Sets the data classification level of this implementation.
     * @param dataClassification Classification string to set
     */
    public void setDataClassification(String dataClassification) { this.dataClassification = dataClassification; }
    
    /**
     * Gets the criticality level of this implementation.
     * @return Criticality level string
     */
    public String getCriticalityLevel() { return criticalityLevel; }
    
    /**
     * Sets the criticality level of this implementation.
     * @param criticalityLevel Criticality string to set
     */
    public void setCriticalityLevel(String criticalityLevel) { this.criticalityLevel = criticalityLevel; }
    
    /**
     * Checks if this implementation is internet-facing.
     * @return true if internet-facing, else false
     */
    public boolean isInternetFacing() { return internetFacing; }
    
    /**
     * Sets whether this implementation is internet-facing.
     * @param internetFacing true if internet-facing, else false
     */
    public void setInternetFacing(boolean internetFacing) { this.internetFacing = internetFacing; }
    
    /**
     * Checks if this implementation processes sensitive customer data.
     * @return true if sensitive customer data, else false
     */
    public boolean isSensitiveCustomerData() { return sensitiveCustomerData; }
    
    /**
     * Sets whether this implementation processes sensitive customer data.
     * @param sensitiveCustomerData true if sensitive customer data, else false
     */
    public void setSensitiveCustomerData(boolean sensitiveCustomerData) { this.sensitiveCustomerData = sensitiveCustomerData; }
    
    /**
     * Gets the risk score assigned to this implementation.
     * @return Risk score string
     */
    public String getRiskScore() { return riskScore; }
    
    /**
     * Sets the risk score assigned to this implementation.
     * @param riskScore Risk score string to set
     */
    public void setRiskScore(String riskScore) { this.riskScore = riskScore; }
    
    /**
     * Gets the version of the system implementation.
     * @return Version string
     */
    public String getVersion() { return version; }
    
    /**
     * Sets the version of the system implementation.
     * @param version Version string to set
     */
    public void setVersion(String version) { this.version = version; }
    
    /**
     * Gets the environment (e.g., production, testing) of this implementation.
     * @return Environment string
     */
    public String getEnvironment() { return environment; }
    
    /**
     * Sets the environment (e.g., production, testing) of this implementation.
     * @param environment Environment string to set
     */
    public void setEnvironment(String environment) { this.environment = environment; }
    
    /**
     * Gets the timestamp when this implementation record was created.
     * @return Created timestamp
     */
    public Timestamp getCreatedAt() { return createdAt; }
    
    /**
     * Sets the timestamp when this implementation record was created.
     * @param createdAt Created timestamp to set
     */
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    /**
     * Gets the timestamp when this implementation record was last updated.
     * @return Updated timestamp
     */
    public Timestamp getUpdatedAt() { return updatedAt; }
    
    /**
     * Sets the timestamp when this implementation record was last updated.
     * @param updatedAt Updated timestamp to set
     */
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
