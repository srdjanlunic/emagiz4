package dto;

import java.sql.Timestamp;
import java.util.UUID;

public class EscalationCreationDto {
    private String cveId;
    private UUID systemId;
    private String reason;
    private UUID techExpertId;
    private UUID securityOfficerId;
    
    public String getCveId() {
        return cveId;
    }
    
    public void setCveId(String cveId) {
        this.cveId = cveId;
    }
    
    public UUID getSystemId() {
        return systemId;
    }
    
    public void setSystemId(UUID systemId) {
        this.systemId = systemId;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public UUID getTechExpertId() {
        return techExpertId;
    }
    
    public void setTechExpertId(UUID techExpertId) {
        this.techExpertId = techExpertId;
    }
    
    public UUID getSecurityOfficerId() {
        return securityOfficerId;
    }
    
    public void setSecurityOfficerId(UUID securityOfficerId) {
        this.securityOfficerId = securityOfficerId;
    }
}
