package model;

import java.sql.Timestamp;
import java.util.UUID;

public class Department {
    private UUID id;
    private String name;
    private String description;
    private UUID organizationId;
    private Timestamp createdAt;

    public Department() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public Department(String name, String description, UUID organizationId) {
        this();
        this.name = name;
        this.description = description;
        this.organizationId = organizationId;
    }

    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public UUID getOrganizationId() { return organizationId; }
    public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
