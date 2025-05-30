package model;

import java.sql.Timestamp;

public class ITSystem {
    private Long id;
    private String name;
    private String description;
    private String version;
    private String vendor;
    private Long departmentId;
    private Long ownerId;
    private String dataClassification;
    private String criticalityLevel;
    private boolean internetFacing;
    private int riskScore;
    private Timestamp createdAt;

    public ITSystem() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.internetFacing = false;
        this.riskScore = 0;
    }

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getVendor() { return vendor; }
    public void setVendor(String vendor) { this.vendor = vendor; }

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public String getDataClassification() { return dataClassification; }
    public void setDataClassification(String dataClassification) { this.dataClassification = dataClassification; }

    public String getCriticalityLevel() { return criticalityLevel; }
    public void setCriticalityLevel(String criticalityLevel) { this.criticalityLevel = criticalityLevel; }

    public boolean isInternetFacing() { return internetFacing; }
    public void setInternetFacing(boolean internetFacing) { this.internetFacing = internetFacing; }

    public int getRiskScore() { return riskScore; }
    public void setRiskScore(int riskScore) { this.riskScore = riskScore; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
