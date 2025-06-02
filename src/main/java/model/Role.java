package model;

import java.sql.Timestamp;
import java.util.UUID;

public class Role {
    private UUID id;
    private String name;
    private String description;
    private Timestamp createdAt;

    public Role() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public Role(String name, String description) {
        this();
        this.name = name;
        this.description = description;
    }

    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
