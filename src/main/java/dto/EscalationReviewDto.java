package dto;

import java.sql.Timestamp;
import java.util.UUID;

public class EscalationReviewDto {
    
    private UUID techExpertId;
    private String response;
    private Timestamp responseDate;
    
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
}
