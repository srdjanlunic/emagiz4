package dto;

import java.util.UUID;

public class SystemRegistrationDto {
    private String name;
    private String description;
    private String vendor;
    private String version;
    private UUID ownerId;
    private boolean internetFacing;
    private String dataClassification;
    private String criticalityLevel;
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getVendor() {
        return vendor;
    }
    
    public void setVendor(String vendor) {
        this.vendor = vendor;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public UUID getOwnerId() {
        return ownerId;
    }
    
    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }
    
    public boolean isInternetFacing() {
        return internetFacing;
    }
    
    public void setInternetFacing(boolean internetFacing) {
        this.internetFacing = internetFacing;
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
} 