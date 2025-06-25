package model;

import java.sql.Timestamp;
import java.util.UUID;

public class Escalation {
    
    private UUID id;
    private UUID systemVulnerabilityId;
    private UUID securityOfficerId;
    private String escalationReason;
    private Timestamp escalationDate;
    private EscalationStatus escalationStatus;
    private UUID techExpertId;
    private String response;
    private Timestamp responseDate;
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
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
    
    public EscalationStatus getEscalationStatus() {
        return escalationStatus;
    }
    
    public UUID getTechExpertId() {
        return techExpertId;
    }
    
    public void setTechExpertId(UUID techExpertId) {
        this.techExpertId = techExpertId;
    }
    
    public String getResponse() {
        return response;
    }
    
    public void setResponse(String response) {
        this.response = response;
    }
    
    public Timestamp getResponseDate() {
        return responseDate;
    }
    
    public void setResponseDate(Timestamp responseDate) {
        this.responseDate = responseDate;
    }
    
    public void setEscalationStatus(EscalationStatus escalationStatus) {
        this.escalationStatus = escalationStatus;
    }
}
