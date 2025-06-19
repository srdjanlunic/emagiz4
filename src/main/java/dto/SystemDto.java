package dto;

import java.util.UUID;
import java.sql.Timestamp;

public class SystemDto {
    private UUID id;
    private String name;
    private String vendor;
    private String description;
    private Timestamp createdAt;

    public SystemDto() {
        // No-argument constructor
    }

    // Constructors, Getters and Setters
    public SystemDto(UUID id, String name, String vendor, String description, Timestamp createdAt) {
        this.id = id;
        this.name = name;
        this.vendor = vendor;
        this.description = description;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
} 