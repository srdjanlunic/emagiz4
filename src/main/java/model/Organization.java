package model;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * Represents an organization within the system.
 * Each organization has a unique ID, a name, and a creation timestamp.
 */
public class Organization {
    private UUID id;
    private String name;
    private Timestamp createdAt;
    
    /**
     * Default constructor that sets the creation timestamp to the current time.
     */
    public Organization() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }
    
    /**
     * Constructs a new organization with the given name.
     * The creation timestamp is set to the current time.
     *
     * @param name the name of the organization
     */
    public Organization(String name) {
        this();
        this.name = name;
    }
    
    /**
     * Gets the unique ID of the organization.
     *
     * @return the organization ID
     */
    public UUID getId() {
        return id;
    }
    
    /**
     * Sets the unique ID of the organization.
     *
     * @param id the organization ID
     */
    public void setId(UUID id) {
        this.id = id;
    }
    
    /**
     * Gets the name of the organization.
     *
     * @return the organization name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the name of the organization.
     *
     * @param name the organization name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Gets the creation timestamp of the organization.
     *
     * @return the timestamp when the organization was created
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Sets the creation timestamp of the organization.
     *
     * @param createdAt the creation timestamp
     */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Returns a string representation of the organization.
     *
     * @return a string containing the organization details
     */
    @Override
    public String toString() {
        return "Organization{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}


