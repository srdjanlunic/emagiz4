package model;

import java.sql.Timestamp;

public class Department {
    private Long id;
    private String name;
    private String description;
    private Long organizationId;
    private Timestamp createdAt;

    public Department() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public Department(String name, String description, Long organizationId) {
        this();
        this.name = name;
        this.description = description;
        this.organizationId = organizationId;
    }

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getOrganizationId() { return organizationId; }
    public void setOrganizationId(Long organizationId) { this.organizationId = organizationId; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
