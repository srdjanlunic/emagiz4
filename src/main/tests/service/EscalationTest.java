package service;

import dao.EscalationDAO;
import dto.EscalationCreationDto;
import dto.EscalationReviewDto;
import model.Escalation;
import model.EscalationStatus;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the EscalationService class, validating core service functionality
 * including creation, review, retrieval, and deletion of escalation records.
 */
public class EscalationTest {
    private EscalationService escalationService;
    private EscalationDAO escalationDAO;
    
    private UUID escalationId;
    private UUID vulnerabilityId;
    private UUID officerId;
    private UUID techExpertId;
    
    /**
     * Prepares a baseline escalation record to be used in most test cases.
     */
    @BeforeEach
    public void setup() {
        escalationService = new EscalationService();
        escalationDAO = new EscalationDAO();
        
        vulnerabilityId = UUID.randomUUID();
        officerId = UUID.randomUUID();
        techExpertId = UUID.randomUUID();
        
        // Create a base escalation record
        EscalationCreationDto dto = new EscalationCreationDto();
        dto.setSystemVulnerabilityId(vulnerabilityId);
        dto.setSecurityOfficerId(officerId);
        dto.setEscalationReason("Initial test escalation");
        dto.setEscalationDate(null); // Timestamp not needed for testing
        
        Escalation escalation = escalationService.create(dto);
        assertNotNull(escalation);
        escalationId = escalation.getId();
    }
    
    /**
     * Tests successful creation of an escalation.
     * Verifies that the returned escalation has a valid ID and correct status/reason.
     */
    @Test
    public void testCreateEscalation_Success() {
        EscalationCreationDto dto = new EscalationCreationDto();
        dto.setSystemVulnerabilityId(UUID.randomUUID());
        dto.setSecurityOfficerId(UUID.randomUUID());
        dto.setEscalationReason("New escalation test");
        dto.setEscalationDate(null);
        
        Escalation escalation = escalationService.create(dto);
        
        assertNotNull(escalation);
        assertNotNull(escalation.getId());
        assertEquals(EscalationStatus.ESCALATED, escalation.getEscalationStatus());
        assertEquals("New escalation test", escalation.getEscalationReason());
        
        // Cleanup this specific test data
        escalationDAO.delete(escalation.getId());
    }
    
    /**
     * Tests that an escalation can be reviewed correctly.
     * Verifies status is updated to REVIEWED and that response and techExpertId are set.
     */
    @Test
    public void testReviewEscalation_UpdatesStatusAndResponse() {
        EscalationReviewDto reviewDto = new EscalationReviewDto();
        reviewDto.setTechExpertId(techExpertId);
        reviewDto.setResponse("Reviewed and approved");
        reviewDto.setResponseDate(null);
        
        Escalation reviewed = escalationService.review(escalationId, reviewDto);
        
        assertNotNull(reviewed);
        assertEquals(EscalationStatus.REVIEWED, reviewed.getEscalationStatus());
        assertEquals("Reviewed and approved", reviewed.getResponse());
        assertEquals(techExpertId, reviewed.getTechExpertId());
    }
    
    /**
     * Tests retrieval of an escalation by its unique ID.
     * Ensures the correct escalation is returned.
     */
    @Test
    public void testFindById_ReturnsCorrectEscalation() {
        Escalation found = escalationService.findById(escalationId);
        
        assertNotNull(found);
        assertEquals(escalationId, found.getId());
    }
    
    /**
     * Tests finding an escalation using the system vulnerability ID.
     * Confirms the returned escalation matches the expected ID.
     */
    @Test
    public void testFindBySystemVulnerabilityId_ReturnsEscalation() {
        Escalation found = escalationService.findBySystemVulnerabilityId(vulnerabilityId);
        
        assertNotNull(found);
        assertEquals(vulnerabilityId, found.getSystemVulnerabilityId());
    }
    
    /**
     * Tests that the service can return a list of escalations associated with a specific security officer.
     * Confirms the list is not empty and contains the baseline escalation.
     */
    @Test
    public void testFindBySecurityOfficer_ReturnsList() {
        List<Escalation> list = escalationService.findBySecurityOfficer(officerId);
        
        assertNotNull(list);
        assertFalse(list.isEmpty());
        assertTrue(list.stream().anyMatch(e -> e.getId().equals(escalationId)));
    }
    
    /**
     * Tests deletion of an escalation record.
     * Verifies it is successfully removed and cannot be found afterward.
     */
    @Test
    public void testDeleteEscalation_RemovesRecord() {
        EscalationCreationDto dto = new EscalationCreationDto();
        dto.setSystemVulnerabilityId(UUID.randomUUID());
        dto.setSecurityOfficerId(UUID.randomUUID());
        dto.setEscalationReason("To be deleted");
        dto.setEscalationDate(null);
        
        Escalation tempEscalation = escalationService.create(dto);
        UUID tempId = tempEscalation.getId();
        
        assertTrue(escalationService.delete(tempId));
        assertNull(escalationService.findById(tempId));
    }
    /**
     * Tests findBySecurityOfficer(UUID): retrieves list of escalations assigned to a specific officer.
     */
    @Test
    public void testFindBySecurityOfficer_ReturnsEscalations() {
        List<Escalation> result = escalationService.findBySecurityOfficer(officerId);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(e -> e.getId().equals(escalationId)));
    }
    
    /**
     * Tests findByTechExpert(UUID): should return list after review assigns techExpertId.
     */
    @Test
    public void testFindByTechExpert_ReturnsReviewedEscalation() {
        // Assign a tech expert through review first
        EscalationReviewDto reviewDto = new EscalationReviewDto();
        reviewDto.setTechExpertId(techExpertId);
        reviewDto.setResponse("Handled by expert");
        reviewDto.setResponseDate(null);
        escalationService.review(escalationId, reviewDto);
        
        List<Escalation> result = escalationService.findByTechExpert(techExpertId);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(e -> e.getId().equals(escalationId)));
    }
    
    /**
     * Tests findAll(): ensures at least one escalation exists (from setup).
     */
    @Test
    public void testFindAll_ReturnsList() {
        List<Escalation> result = escalationService.findAll();
        assertNotNull(result);
        assertTrue(result.stream().anyMatch(e -> e.getId().equals(escalationId)));
    }
    /**
     * Cleans up the baseline escalation created during setup.
     */
    @AfterEach
    public void cleanup() {
        if (escalationId != null) {
            escalationDAO.delete(escalationId);
        }
    }
}
