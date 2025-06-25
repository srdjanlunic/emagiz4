package model;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * Represents a role assigned to users, defining their permissions and access level.
 */
public class Role {
    private UUID id;
    private String name;
    private String description;
    private Timestamp createdAt;
    
    /**
     * Default constructor initializes createdAt to current timestamp.
     */
    public Role() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }
    
    /**
     * Constructs a Role with specified name and description.
     *
     * @param name Role name
     * @param description Role description
     */
    public Role(String name, String description) {
        this();
        this.name = name;
        this.description = description;
    }
    
    /**
     * Returns the unique identifier of the role.
     * @return Role UUID
     */
    public UUID getId() { return id; }
    
    /**
     * Sets the unique identifier of the role.
     * @param id Role UUID
     */
    public void setId(UUID id) { this.id = id; }
    
    /**
     * Returns the name of the role.
     * @return Role name
     */
    public String getName() { return name; }
    
    /**
     * Sets the name of the role.
     * @param name Role name
     */
    public void setName(String name) { this.name = name; }
    
    /**
     * Returns the description of the role.
     * @return Role description
     */
    public String getDescription() { return description; }
    
    /**
     * Sets the description of the role.
     * @param description Role description
     */
    public void setDescription(String description) { this.description = description; }
    
    /**
     * Returns the timestamp when the role was created.
     * @return Creation timestamp
     */
    public Timestamp getCreatedAt() { return createdAt; }
    
    /**
     * Sets the timestamp when the role was created.
     * @param createdAt Creation timestamp
     */
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
