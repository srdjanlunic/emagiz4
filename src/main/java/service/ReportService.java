package service;

import com.lowagie.text.*;
import dao.*;
import java.io.FileNotFoundException;
import java.util.HashMap;
import model.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;
import java.io.FileOutputStream;


public class ReportService {
    
    private final VulnerabilityDAO cveDAO = new VulnerabilityDAO();
    private final VulnerabilityMatchDAO matchDAO = new VulnerabilityMatchDAO();
    private final VulnerabilityAssessmentDAO assessmentDAO = new VulnerabilityAssessmentDAO();
    private final SystemVulnerabilityDAO systemVulnerabilityDAO = new SystemVulnerabilityDAO();
    private final SystemOwnerDAO sysOwnerDAO = new SystemOwnerDAO();
    private final SystemDAO systemDAO = new SystemDAO();
    private final EscalationDAO escalationDAO = new EscalationDAO();

    /**
     * Generate a dashboard summary where each systemImplementationId maps to:
     *   "openCount"     → number of matches whose latest assessment is not RESOLVED
     *   "resolvedCount" → total matches minus openCount
     */
    public Map<UUID, Map<String, Long>> generateDashboard() {
        // 1. load all vulnerability-system matches
        List<VulnerabilityMatch> allMatches = matchDAO.findAll();

        // 2. group by systemImplementationId
        return allMatches.stream()
                .collect(Collectors.groupingBy(
                        VulnerabilityMatch::getSystemImplementationId,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                (List<VulnerabilityMatch> matchesForSystem) -> {
                                    // For each match, fetch its assessments list and take the most recent (first) entry
                                    long openCount = matchesForSystem.stream()
                                            .map((VulnerabilityMatch m) -> {
                                                // DAO returns List<VulnerabilityAssessment> ordered by date desc
                                                List<VulnerabilityAssessment> assessments = VulnerabilityAssessmentDAO.findByMatch(m.getId());
                                                return assessments.isEmpty()
                                                        ? null
                                                        : assessments.get(0); // most recent assessment
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
    
    public void generateSystemOwnerReport(UUID userId) {
        var systemsList = sysOwnerDAO.findImplementationsByOwner(userId);
        var sysVulnerabilitiesMap = new HashMap<SystemImplementation, List<SystemVulnerability>>();
        
        for (SystemImplementation system : systemsList) {
            var vulnerabilities = systemVulnerabilityDAO.findBySystemId(system.getSystemId());
            sysVulnerabilitiesMap.put(system, vulnerabilities);
        }
        
        var document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream("system_owner_report.pdf"));
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
                    
                    table.addCell(cveId);
                    table.addCell(vulnerability.getStatus());
                    table.addCell(vulnerability.getEscalationStatus().getValue());
                    table.addCell(vulnerability.getNotes());
                    table.addCell(vulnerability.getAssessmentDate().toString());
                    table.addCell("");
                    table.addCell("");
                }
            }
            
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void generateSystemsReport() {
        var systemsList = systemDAO.findAll();
        
        var document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream("systems_report.pdf"));
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
            
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void generateVulnerabilityReport() {
        var vulnerabilitiesList = cveDAO.findAll();
        
        var document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream("cve_report.pdf"));
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
            
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void generateEscalationReport() {
        var escalationsList = escalationDAO.getAllEscalations();
    }
}

