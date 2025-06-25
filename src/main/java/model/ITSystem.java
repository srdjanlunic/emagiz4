package model;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * Represents an IT system entity within the application.
 */
public class ITSystem {
    /** Unique identifier of the system. */
    private UUID id;
    
    /** Name of the system. */
    private String name;
    
    /** Vendor providing the system. */
    private String vendor;
    
    /** Description of the system. */
    private String description;
    
    /** Timestamp when the system was created. */
    private Timestamp createdAt;
    
    /**
     * Default constructor that sets the createdAt timestamp to the current time.
     */
    public ITSystem() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }
    
    /**
     * Constructs a new ITSystem with the specified details.
     *
     * @param name        the name of the system
     * @param vendor      the system's vendor
     * @param description a brief description of the system
     */
    public ITSystem(String name, String vendor, String description) {
        this();
        this.name = name;
        this.vendor = vendor;
        this.description = description;
    }
    
    // Getters and setters
    
    /**
     * @return the unique identifier of the system
     */
    public UUID getId() { return id; }
    
    /**
     * @param id the unique identifier to set
     */
    public void setId(UUID id) { this.id = id; }
    
    /**
     * @return the system's name
     */
    public String getName() { return name; }
    
    /**
     * @param name the system's name to set
     */
    public void setName(String name) { this.name = name; }
    
    /**
     * @return the vendor of the system
     */
    public String getVendor() { return vendor; }
    
    /**
     * @param vendor the vendor to set
     */
    public void setVendor(String vendor) { this.vendor = vendor; }
    
    /**
     * @return the system's description
     */
    public String getDescription() { return description; }
    
    /**
     * @param description the system's description to set
     */
    public void setDescription(String description) { this.description = description; }
    
    /**
     * @return the timestamp when the system was created
     */
    public Timestamp getCreatedAt() { return createdAt; }
    
    /**
     * @param createdAt the timestamp to set as the creation time
     */
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
