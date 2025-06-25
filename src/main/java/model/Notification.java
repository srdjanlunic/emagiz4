package model;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * Represents a notification sent to a user about a vulnerability or system event.
 */
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
    
    /**
     * Default constructor sets createdAt to current time and marks unread.
     */
    public Notification() {
        this.isRead = false;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }
    
    /** @return notification id */
    public UUID getId() { return id; }
    
    /** @param id notification id */
    public void setId(UUID id) { this.id = id; }
    
    /** @return user id */
    public UUID getUserId() { return userId; }
    
    /** @param userId user id */
    public void setUserId(UUID userId) { this.userId = userId; }
    
    /** @return match id */
    public UUID getMatchId() { return matchId; }
    
    /** @param matchId match id */
    public void setMatchId(UUID matchId) { this.matchId = matchId; }
    
    /** @return system id */
    public UUID getSystemId() { return systemId; }
    
    /** @param systemId system id */
    public void setSystemId(UUID systemId) { this.systemId = systemId; }
    
    /** @return vulnerability id */
    public UUID getVulnerabilityId() { return vulnerabilityId; }
    
    /** @param vulnerabilityId vulnerability id */
    public void setVulnerabilityId(UUID vulnerabilityId) { this.vulnerabilityId = vulnerabilityId; }
    
    /** @return message */
    public String getMessage() { return message; }
    
    /** @param message message */
    public void setMessage(String message) { this.message = message; }
    
    /** @return type */
    public String getType() { return type; }
    
    /** @param type type */
    public void setType(String type) { this.type = type; }
    
    /** @return priority */
    public String getPriority() { return priority; }
    
    /** @param priority priority */
    public void setPriority(String priority) { this.priority = priority; }
    
    /** @return true if read */
    public boolean isRead() { return isRead; }
    
    /** @param read true if read */
    public void setRead(boolean read) { isRead = read; }
    
    /** @return read timestamp */
    public Timestamp getReadAt() { return readAt; }
    
    /** @param readAt read timestamp */
    public void setReadAt(Timestamp readAt) { this.readAt = readAt; }
    
    /** @return created timestamp */
    public Timestamp getCreatedAt() { return createdAt; }
    
    /** @param createdAt created timestamp */
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
