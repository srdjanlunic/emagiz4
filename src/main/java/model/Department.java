package model;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * Represents a department within an organization.
 */
public class Department {
    private UUID id;
    private String name;
    private String description;
    private UUID organizationId;
    private Timestamp createdAt;
    
    /**
     * Default constructor sets createdAt to current time.
     */
    public Department() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }
    
    /**
     * Constructs a Department with given details.
     *
     * @param name name of the department
     * @param description description of the department
     * @param organizationId id of the parent organization
     */
    public Department(String name, String description, UUID organizationId) {
        this();
        this.name = name;
        this.description = description;
        this.organizationId = organizationId;
    }
    
    /** @return department id */
    public UUID getId() { return id; }
    
    /** @param id department id */
    public void setId(UUID id) { this.id = id; }
    
    /** @return department name */
    public String getName() { return name; }
    
    /** @param name department name */
    public void setName(String name) { this.name = name; }
    
    /** @return department description */
    public String getDescription() { return description; }
    
    /** @param description department description */
    public void setDescription(String description) { this.description = description; }
    
    /** @return organization id */
    public UUID getOrganizationId() { return organizationId; }
    
    /** @param organizationId organization id */
    public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }
    
    /** @return created timestamp */
    public Timestamp getCreatedAt() { return createdAt; }
    
    /** @param createdAt created timestamp */
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
