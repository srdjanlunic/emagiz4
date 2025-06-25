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
}
