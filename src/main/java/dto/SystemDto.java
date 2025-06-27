package dto;

import java.util.UUID;
import java.sql.Timestamp;

/**
 * Data Transfer Object for a system entity.
 */
public class SystemDto {
    private UUID id;
    private String name;
    private String vendor;
    private String description;
    private Timestamp createdAt;
    
    // Implementation fields
    private String version;
    private String environment;
    private int riskScore;
    private String dataClassification;
    private String criticalityLevel;
    private boolean internetFacing;
    private boolean sensitiveCustomerData;
    private UUID ownerId;
    
    /**
     * Default constructor.
     */
    public SystemDto() {
        // No-argument constructor
    }
    
    /**
     * Constructs a SystemDto with all fields.
     *
     * @param id          System ID
     * @param name        System name
     * @param vendor      Vendor name
     * @param description System description
     * @param createdAt   Creation timestamp
     */
    public SystemDto(UUID id, String name, String vendor, String description, Timestamp createdAt) {
        this.id = id;
        this.name = name;
        this.vendor = vendor;
        this.description = description;
        this.createdAt = createdAt;
    }
    
    /** @return System ID */
    public UUID getId() {
        return id;
    }
    
    /** @param id System ID */
    public void setId(UUID id) {
        this.id = id;
    }
    
    /** @return System name */
    public String getName() {
        return name;
    }
    
    /** @param name System name */
    public void setName(String name) {
        this.name = name;
    }
    
    /** @return Vendor name */
    public String getVendor() {
        return vendor;
    }
    
    /** @param vendor Vendor name */
    public void setVendor(String vendor) {
        this.vendor = vendor;
    }
    
    /** @return System description */
    public String getDescription() {
        return description;
    }
    
    /** @param description System description */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /** @return Creation timestamp */
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    /** @param createdAt Creation timestamp */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    /** @return System version */
    public String getVersion() {
        return version;
    }
    
    /** @param version System version */
    public void setVersion(String version) {
        this.version = version;
    }
    
    /** @return System environment */
    public String getEnvironment() {
        return environment;
    }
    
    /** @param environment System environment */
    public void setEnvironment(String environment) {
        this.environment = environment;
    }
    
    /** @return Risk score */
    public int getRiskScore() {
        return riskScore;
    }
    
    /** @param riskScore Risk score */
    public void setRiskScore(int riskScore) {
        this.riskScore = riskScore;
    }
    
    /** @return Data classification */
    public String getDataClassification() {
        return dataClassification;
    }
    
    /** @param dataClassification Data classification */
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
    
    /** @return True if internet facing */
    public boolean isInternetFacing() {
        return internetFacing;
    }
    
    /** @param internetFacing Internet facing flag */
    public void setInternetFacing(boolean internetFacing) {
        this.internetFacing = internetFacing;
    }
    
    /** @return True if handles sensitive customer data */
    public boolean isSensitiveCustomerData() {
        return sensitiveCustomerData;
    }
    
    /** @param sensitiveCustomerData Sensitive customer data flag */
    public void setSensitiveCustomerData(boolean sensitiveCustomerData) {
        this.sensitiveCustomerData = sensitiveCustomerData;
    }
    
    /** @return Owner ID */
    public UUID getOwnerId() {
        return ownerId;
    }
    
    /** @param ownerId Owner ID */
    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }
}
