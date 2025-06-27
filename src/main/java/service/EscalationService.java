package service;

import dao.EscalationDAO;
import dto.EscalationCreationDto;
import dto.EscalationReviewDto;
import java.util.List;
import java.util.UUID;
import model.Escalation;
import model.EscalationStatus;
import dao.VulnerabilityDAO;
import dao.SystemVulnerabilityDAO;
import model.Vulnerability;
import model.SystemVulnerability;
import java.sql.Timestamp;

public class EscalationService {
    
    private EscalationDAO escalationDAO = new EscalationDAO();
    private VulnerabilityDAO vulnerabilityDAO = new VulnerabilityDAO();
    private SystemVulnerabilityDAO systemVulnerabilityDAO = new SystemVulnerabilityDAO();
    
    public Escalation create(EscalationCreationDto escalationCreation) {
        System.out.println("Creating escalation for CVE: " + escalationCreation.getCveId() + ", System: " + escalationCreation.getSystemId());
        
        Vulnerability vulnerability = vulnerabilityDAO.findByCveId(escalationCreation.getCveId());
        if (vulnerability == null) {
            System.out.println("Vulnerability not found for CVE: " + escalationCreation.getCveId());
            return null;
        }

        if (escalationCreation.getSystemId() == null) {
            System.out.println("System ID is null in escalation request");
            return null;
        }

        SystemVulnerability sv = systemVulnerabilityDAO.findBySystemAndVulnerability(escalationCreation.getSystemId(), vulnerability.getId());
        if (sv == null) {
            System.out.println("Creating new SystemVulnerability record for system: " + escalationCreation.getSystemId() + ", vulnerability: " + vulnerability.getId());
            // Create SystemVulnerability record if it doesn't exist
            sv = new SystemVulnerability();
            sv.setId(UUID.randomUUID());
            sv.setSystemId(escalationCreation.getSystemId());
            sv.setVulnerabilityId(vulnerability.getId());
            sv.setStatus("open");
            sv.setAssessmentDate(new Timestamp(System.currentTimeMillis()));
            systemVulnerabilityDAO.create(sv);
            // sv should now have the correct ID and be ready for use
        }
        
        var escalation = new Escalation();
        
        escalation.setSystemVulnerabilityId(sv.getId());
        escalation.setSecurityOfficerId(escalationCreation.getSecurityOfficerId());
        escalation.setEscalationReason(escalationCreation.getReason());
        escalation.setEscalationDate(new Timestamp(System.currentTimeMillis()));
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
        return escalationDAO.findByTechExpertOrUnassigned(techExpertid);
    }
    
    public List<Escalation> findAll() {
        return escalationDAO.findAll();
    }
}