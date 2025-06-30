package dto;

import java.sql.Timestamp;
import java.util.UUID;
import model.EscalationStatus;

public class EscalationDto {

    private UUID id;
    private UUID systemVulnerabilityId;
    private UUID securityOfficerId;
    private String reason;
    private Timestamp escalationDate;
    private EscalationStatus status;
    private UUID techExpertId;
    private String response;
    private Timestamp responseDate;
    private String systemName;
    private String cveId;

    public EscalationDto() {}

    // Getters and Setters
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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Timestamp getEscalationDate() {
        return escalationDate;
    }

    public void setEscalationDate(Timestamp escalationDate) {
        this.escalationDate = escalationDate;
    }

    public EscalationStatus getStatus() {
        return status;
    }

    public void setStatus(EscalationStatus status) {
        this.status = status;
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

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getCveId() {
        return cveId;
    }

    public void setCveId(String cveId) {
        this.cveId = cveId;
    }
} 