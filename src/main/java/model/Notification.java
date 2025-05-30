package model;

import java.sql.Timestamp;

public class Notification {
    private Long id;
    private String message;
    private Long userId;
    private Long systemId;
    private Long vulnerabilityId;
    private boolean isRead;
    private String type;
    private Timestamp createdAt;

    public Notification() {
        this.isRead = false;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getSystemId() { return systemId; }
    public void setSystemId(Long systemId) { this.systemId = systemId; }

    public Long getVulnerabilityId() { return vulnerabilityId; }
    public void setVulnerabilityId(Long vulnerabilityId) { this.vulnerabilityId = vulnerabilityId; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
