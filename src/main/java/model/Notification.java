package model;

import java.sql.Timestamp;
import java.util.UUID;

public class Notification {
    private UUID id;
    private UUID userId;
    private UUID matchId;
    private UUID systemId;
    private UUID vulnerabilityId;
    private String message;
    private String type;
    private String priority;
    private boolean isRead;
    private Timestamp readAt;
    private Timestamp createdAt;

    public Notification() {
        this.isRead = false;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public UUID getMatchId() { return matchId; }
    public void setMatchId(UUID matchId) { this.matchId = matchId; }

    public UUID getSystemId() { return systemId; }
    public void setSystemId(UUID systemId) { this.systemId = systemId; }

    public UUID getVulnerabilityId() { return vulnerabilityId; }
    public void setVulnerabilityId(UUID vulnerabilityId) { this.vulnerabilityId = vulnerabilityId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public Timestamp getReadAt() { return readAt; }
    public void setReadAt(Timestamp readAt) { this.readAt = readAt; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
