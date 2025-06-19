package service;

import dao.VulnerabilityMatchDAO;
import dao.VulnerabilityAssessmentDAO;
import model.VulnerabilityMatch;
import model.VulnerabilityAssessment;
import model.AssessmentStatus;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class ReportService {

    private final VulnerabilityMatchDAO matchDAO = new VulnerabilityMatchDAO();
    private final VulnerabilityAssessmentDAO assessmentDAO = new VulnerabilityAssessmentDAO();

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
}

