package service;

import com.lowagie.text.*;
import dao.*;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import model.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPTable;

/**
 * Service to generate various reports and dashboard data.
 */
public class ReportService {
    
    private final VulnerabilityDAO cveDAO = new VulnerabilityDAO();
    private final VulnerabilityMatchDAO matchDAO = new VulnerabilityMatchDAO();
    private final SystemVulnerabilityDAO systemVulnerabilityDAO = new SystemVulnerabilityDAO();
    private final SystemOwnerDAO sysOwnerDAO = new SystemOwnerDAO();
    private final SystemDAO systemDAO = new SystemDAO();
    private final EscalationDAO escalationDAO = new EscalationDAO();
    private final UserDAO userDAO = new UserDAO();
    
    /**
     * Generate a dashboard summary that maps each system implementation ID to counts of open and resolved vulnerabilities.
     * <p>
     * "openCount" is the number of matches whose latest assessment is not RESOLVED.
     * "resolvedCount" is the total matches minus openCount.
     *
     * @return Map where key is systemImplementationId and value is a map of counts with keys "openCount" and "resolvedCount"
     */
    public Map<UUID, Map<String, Long>> generateDashboard() {
        List<VulnerabilityMatch> allMatches = matchDAO.findAll();
        
        return allMatches.stream()
                .collect(Collectors.groupingBy(
                        VulnerabilityMatch::getSystemImplementationId,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                (List<VulnerabilityMatch> matchesForSystem) -> {
                                    long openCount = matchesForSystem.stream()
                                            .map((VulnerabilityMatch m) -> {
                                                List<VulnerabilityAssessment> assessments = VulnerabilityAssessmentDAO.findByMatch(m.getId());
                                                return assessments.isEmpty()
                                                        ? null
                                                        : assessments.get(0);
                                            })
                                            .filter(a -> a == null
                                                    || !AssessmentStatus.ACCEPTED_RISK.equals(a.getStatus()))
                                            .count();
                                    
                                    long totalMatches = matchesForSystem.size();
                                    long resolvedCount = totalMatches - openCount;
                                    
                                    return Map.of("openCount", openCount,
                                                  "resolvedCount", resolvedCount);
                                }
                        )
                ));
    }
    
    /**
     * Generate a PDF report for a given system owner.
     *
     * @param userId the UUID of the system owner
     * @return a byte array representing the generated PDF report
     */
    public byte[] generateSystemOwnerReport(UUID userId) {
        var systemsList = sysOwnerDAO.findImplementationsByOwner(userId);
        var sysVulnerabilitiesMap = new HashMap<SystemImplementation, List<SystemVulnerability>>();
        
        for (SystemImplementation system : systemsList) {
            var vulnerabilities = systemVulnerabilityDAO.findBySystemId(system.getSystemId());
            sysVulnerabilitiesMap.put(system, vulnerabilities);
        }
        
        var document = new Document();
        var byteArrayStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, byteArrayStream);
        document.open();
        
        document.add(new Paragraph("System Owner Report"));
        document.add(new Paragraph(" "));
        
        for (Map.Entry<SystemImplementation, List<SystemVulnerability>> entry : sysVulnerabilitiesMap.entrySet()) {
            var table = new PdfPTable(7);
            var implem = entry.getKey();
            var system = systemDAO.findById(implem.getSystemId());
            Font boldFont = new Font(Font.BOLD);
            
            table.addCell(new Phrase(system.getName(), boldFont));
            table.addCell(new Phrase(implem.getDataClassification(), boldFont));
            table.addCell(new Phrase(implem.getCriticalityLevel(), boldFont));
            table.addCell(new Phrase("Sensitive:" + implem.isSensitiveCustomerData(), boldFont));
            table.addCell(new Phrase("Risk: " + implem.getRiskScore(), boldFont));
            table.addCell(new Phrase(implem.getVersion(), boldFont));
            table.addCell(new Phrase(implem.getEnvironment(), boldFont));
            
            var vulnerabilities = entry.getValue();
            for (SystemVulnerability vulnerability : vulnerabilities) {
                var cveId = cveDAO.findById(vulnerability.getVulnerabilityId()).getCveId();
                var escalation = escalationDAO.findBySystemVulnerabilityId(vulnerability.getVulnerabilityId());
                
                table.addCell(cveId);
                table.addCell(vulnerability.getStatus());
                table.addCell(escalation.getEscalationStatus().getValue());
                table.addCell(vulnerability.getNotes());
                table.addCell(vulnerability.getAssessmentDate().toString());
                table.addCell("");
                table.addCell("");
            }
            
            document.add(table);
        }
        
        document.close();
        return byteArrayStream.toByteArray();
    }
    
    /**
     * Generate a PDF report listing all systems.
     *
     * @return a byte array representing the generated PDF report
     */
    public byte[] generateSystemsReport() {
        var systemsList = systemDAO.findAll();
        
        var document = new Document();
        var byteArrayStream = new ByteArrayOutputStream();
        
        PdfWriter.getInstance(document, byteArrayStream);
        document.open();
        
        document.add(new Paragraph("Systems Report"));
        document.add(new Paragraph(" "));
        
        var table = new PdfPTable(5);
        
        for (ITSystem system : systemsList) {
            var cveCount = systemVulnerabilityDAO.findBySystemId(system.getId()).size();
            
            table.addCell(system.getName());
            table.addCell(system.getVendor());
            table.addCell(system.getDescription());
            table.addCell(system.getCreatedAt().toString());
            table.addCell(String.valueOf(cveCount));
        }
        
        document.add(table);
        document.close();
        return byteArrayStream.toByteArray();
    }
    
    /**
     * Generate a PDF report listing all vulnerabilities.
     *
     * @return a byte array representing the generated PDF report
     */
    public byte[] generateVulnerabilitiesReport() {
        var vulnerabilitiesList = cveDAO.findAll();
        
        var document = new Document();
        var byteArrayStream = new ByteArrayOutputStream();
        
        PdfWriter.getInstance(document, byteArrayStream);
        document.open();
        
        document.add(new Paragraph("Vulnerabilities Report"));
        document.add(new Paragraph(" "));
        
        var table = new PdfPTable(5);
        
        for (Vulnerability vulnerability : vulnerabilitiesList) {
            table.addCell(vulnerability.getCveId());
            table.addCell(vulnerability.getDescription());
            table.addCell(vulnerability.getSeverity());
            table.addCell("Published: " + vulnerability.getPublishedDate());
            table.addCell("Updated: " + vulnerability.getLastModifiedDate());
        }
        
        document.add(table);
        document.close();
        return byteArrayStream.toByteArray();
    }
    
    /**
     * Generate a PDF report listing all escalations.
     *
     * @return a byte array representing the generated PDF report
     */
    public byte[] generateEscalationsReport() {
        var escalationsList = escalationDAO.findAll();
        
        var document = new Document();
        var byteArrayStream = new ByteArrayOutputStream();
        
        PdfWriter.getInstance(document, byteArrayStream);
        document.open();
        
        document.add(new Paragraph("Escalations Report"));
        document.add(new Paragraph(" "));
        
        var table = new PdfPTable(9);
        
        for (Escalation escalation : escalationsList) {
            var systemVulnerability = systemVulnerabilityDAO.findById(escalation.getSystemVulnerabilityId());
            var cveId = cveDAO.findById(systemVulnerability.getVulnerabilityId()).getCveId();
            var system = systemDAO.findById(systemVulnerability.getSystemId());
            var securityOfficer = userDAO.findById(escalation.getSecurityOfficerId());
            var techExpert = userDAO.findById(escalation.getTechExpertId());
            
            table.addCell(cveId);
            table.addCell(system.getName());
            table.addCell(escalation.getEscalationStatus().getValue());
            table.addCell("Escalated by: " + securityOfficer.getUsername());
            table.addCell("Reason: " + escalation.getEscalationReason());
            table.addCell("Escalation date: " + escalation.getEscalationDate());
            table.addCell("Reviewed by: " + techExpert.getUsername());
            table.addCell("Response: " + escalation.getResponse());
            table.addCell("Response date: " + escalation.getResponseDate());
        }
        
        document.add(table);
        document.close();
        return byteArrayStream.toByteArray();
    }
}
