package model;

import java.sql.Timestamp;
import java.util.UUID;

public class AssessmentHistory {
    private UUID id;
    private UUID assessmentId;
    private UUID changedBy;
    private AssessmentStatus oldStatus;
    private AssessmentStatus newStatus;
    private String changeReason;
    private Timestamp changedAt;

    public AssessmentHistory() {
        this.changedAt = new Timestamp(System.currentTimeMillis());
    }

    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getAssessmentId() { return assessmentId; }
    public void setAssessmentId(UUID assessmentId) { this.assessmentId = assessmentId; }

    public UUID getChangedBy() { return changedBy; }
    public void setChangedBy(UUID changedBy) { this.changedBy = changedBy; }

    public AssessmentStatus getOldStatus() { return oldStatus; }
    public void setOldStatus(AssessmentStatus oldStatus) { this.oldStatus = oldStatus; }

    public AssessmentStatus getNewStatus() { return newStatus; }
    public void setNewStatus(AssessmentStatus newStatus) { this.newStatus = newStatus; }

    public String getChangeReason() { return changeReason; }
    public void setChangeReason(String changeReason) { this.changeReason = changeReason; }

    public Timestamp getChangedAt() { return changedAt; }
    public void setChangedAt(Timestamp changedAt) { this.changedAt = changedAt; }
}
