package model;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * Represents the history of changes to an assessment's status.
 */
public class AssessmentHistory {
    private UUID id;
    private UUID assessmentId;
    private UUID changedBy;
    private AssessmentStatus oldStatus;
    private AssessmentStatus newStatus;
    private String changeReason;
    private Timestamp changedAt;
    
    /**
     * Default constructor sets changedAt to now.
     */
    public AssessmentHistory() {
        this.changedAt = new Timestamp(System.currentTimeMillis());
    }
    
    /** @return history record id */
    public UUID getId() { return id; }
    
    /** @param id history record id */
    public void setId(UUID id) { this.id = id; }
    
    /** @return assessment id */
    public UUID getAssessmentId() { return assessmentId; }
    
    /** @param assessmentId assessment id */
    public void setAssessmentId(UUID assessmentId) { this.assessmentId = assessmentId; }
    
    /** @return id of user who changed status */
    public UUID getChangedBy() { return changedBy; }
    
    /** @param changedBy id of user who changed status */
    public void setChangedBy(UUID changedBy) { this.changedBy = changedBy; }
    
    /** @return old status */
    public AssessmentStatus getOldStatus() { return oldStatus; }
    
    /** @param oldStatus old status */
    public void setOldStatus(AssessmentStatus oldStatus) { this.oldStatus = oldStatus; }
    
    /** @return new status */
    public AssessmentStatus getNewStatus() { return newStatus; }
    
    /** @param newStatus new status */
    public void setNewStatus(AssessmentStatus newStatus) { this.newStatus = newStatus; }
    
    /** @return reason for change */
    public String getChangeReason() { return changeReason; }
    
    /** @param changeReason reason for change */
    public void setChangeReason(String changeReason) { this.changeReason = changeReason; }
    
    /** @return timestamp of change */
    public Timestamp getChangedAt() { return changedAt; }
    
    /** @param changedAt timestamp of change */
    public void setChangedAt(Timestamp changedAt) { this.changedAt = changedAt; }
}
