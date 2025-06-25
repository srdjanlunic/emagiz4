package service;

import dao.ReportLogDAO;
import model.ReportLog;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

/**
 * Service class for managing report logs and import runs.
 */
public class ReportLogService {
    private final ReportLogDAO dao = new ReportLogDAO();
    
    /**
     * Logs a minimal import run entry (e.g., nightly import).
     *
     * @param userId the ID of the user who initiated the import
     * @param type   the type of import (e.g., "CVE_IMPORT")
     * @param filter any filter or metadata about the import run
     */
    public void logImportRun(UUID userId, String type, String filter) {
        try {
            dao.createLog(userId, type, filter);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Logs a full report entry with detailed metadata.
     *
     * @param rl the ReportLog object containing report details
     * @return the created ReportLog with assigned ID
     */
    public ReportLog logReport(ReportLog rl) {
        try {
            return dao.create(rl);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Retrieves the timestamp of the last import run for a given type.
     *
     * @param type the import type to query (e.g., "CVE_IMPORT")
     * @return the timestamp of the last import run, or null if none found
     */
    public Timestamp getLastRun(String type) {
        try {
            return dao.findLastByType(type);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Lists all report logs in the system.
     *
     * @return a list of all ReportLog entries
     */
    public List<ReportLog> listAllReportLogs() {
        try {
            return dao.findAll();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
