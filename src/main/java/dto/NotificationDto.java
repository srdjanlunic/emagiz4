package dto;

import java.util.UUID;
import java.sql.Timestamp;

/**
 * Data Transfer Object for a user notification.
 */
public class NotificationDto {
    private UUID id;
    private UUID userId;
    private String message;
    private String type;
    private boolean isRead;
    private Timestamp createdAt;
    
    /**
     * Constructs a new NotificationDto.
     *
     * @param id         Notification ID
     * @param userId     ID of the user who receives the notification
     * @param message    Message content
     * @param type       Type of notification
     * @param isRead     Whether the notification has been read
     * @param createdAt  When the notification was created
     */
    public NotificationDto(UUID id, UUID userId, String message, String type, boolean isRead, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.type = type;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }
    
    /** @return Notification ID */
    public UUID getId() {
        return id;
    }
    
    /** @param id Notification ID */
    public void setId(UUID id) {
        this.id = id;
    }
    
    /** @return User ID associated with the notification */
    public UUID getUserId() {
        return userId;
    }
    
    /** @param userId User ID associated with the notification */
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
    
    /** @return The message content */
    public String getMessage() {
        return message;
    }
    
    /** @param message The message content */
    public void setMessage(String message) {
        this.message = message;
    }
    
    /** @return The type of notification */
    public String getType() {
        return type;
    }
    
    /** @param type The type of notification */
    public void setType(String type) {
        this.type = type;
    }
    
    /** @return Whether the notification has been read */
    public boolean isRead() {
        return isRead;
    }
    
    /** @param isRead Set the read status of the notification */
    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }
    
    /** @return Timestamp of when the notification was created */
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    /** @param createdAt Timestamp of when the notification was created */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
