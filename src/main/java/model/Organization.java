package model;

import java.sql.Timestamp;
import java.util.UUID;

public class Organization {
    private UUID id;
    private String name;
    private Timestamp createdAt;

    public Organization() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public Organization(String name) {
        this();
        this.name = name;
    }

    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Organization{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
