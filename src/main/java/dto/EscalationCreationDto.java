package dto;

import java.sql.Timestamp;
import java.util.UUID;

public class EscalationCreationDto {
    private UUID systemVulnerabilityId;
    private UUID securityOfficerId;
    private String escalationReason;
    private Timestamp escalationDate;
    
    public UUID getSystemVulnerabilityId() {
        return systemVulnerabilityId;
    }
    
    public void setSystemVulnerabilityId(UUID systemVulnerabilityId) {
        this.systemVulnerabilityId = systemVulnerabilityId;
    }
    
    public UUID getSecurityOfficerId() {
        return securityOfficerId;
    }
    
    public void setSecurityOfficerId(UUID securityOfficerId) {
        this.securityOfficerId = securityOfficerId;
    }
    
    public String getEscalationReason() {
        return escalationReason;
    }
    
    public void setEscalationReason(String escalationReason) {
        this.escalationReason = escalationReason;
    }
    
    public Timestamp getEscalationDate() {
        return escalationDate;
    }
    
    public void setEscalationDate(Timestamp escalationDate) {
        this.escalationDate = escalationDate;
    }
}
