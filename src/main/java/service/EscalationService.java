package service;

import dao.EscalationDAO;
import dto.EscalationCreationDto;
import dto.EscalationReviewDto;
import java.util.List;
import java.util.UUID;
import model.Escalation;
import model.EscalationStatus;

public class EscalationService {
    
    private EscalationDAO escalationDAO = new EscalationDAO();
    
    public Escalation create(EscalationCreationDto escalationCreation) {
        var escalation = new Escalation();
        
        escalation.setSystemVulnerabilityId(escalationCreation.getSystemVulnerabilityId());
        escalation.setSecurityOfficerId(escalationCreation.getSecurityOfficerId());
        escalation.setEscalationReason(escalationCreation.getEscalationReason());
        escalation.setEscalationDate(escalationCreation.getEscalationDate());
        escalation.setEscalationStatus(EscalationStatus.ESCALATED);
        
        return escalationDAO.create(escalation);
    }
    
    public Escalation review(UUID id, EscalationReviewDto escalationReview) {
        var escalation = escalationDAO.findById(id);
        
        escalation.setTechExpertId(escalationReview.getTechExpertId());
        escalation.setResponse(escalationReview.getResponse());
        escalation.setResponseDate(escalationReview.getResponseDate());
        escalation.setEscalationStatus(EscalationStatus.REVIEWED);
        
        return escalationDAO.update(escalation);
    }
    
    public Escalation update(Escalation escalation) {
        return escalationDAO.update(escalation);
    }
    
    public boolean delete(UUID id) {
        return escalationDAO.delete(id);
    }
    
    public Escalation findById(UUID id) {
        return escalationDAO.findById(id);
    }
    
    public Escalation findBySystemVulnerabilityId(UUID systemVulnerabilityId) {
        return escalationDAO.findBySystemVulnerabilityId(systemVulnerabilityId);
    }
    
    public List<Escalation> findBySecurityOfficer(UUID securityOfficerId) {
        return escalationDAO.findBySecurityOfficer(securityOfficerId);
    }
    
    public List<Escalation> findByTechExpert(UUID techExpertid) {
        return escalationDAO.findByTechExpert(techExpertid);
    }
    
    public List<Escalation> findAll() {
        return escalationDAO.findAll();
    }
}