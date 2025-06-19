package service;

import dao.ReportLogDAO;
import model.ReportLog;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public class ReportLogService {
    private final ReportLogDAO dao = new ReportLogDAO();

    /** Minimal log (as your nightly import) */
    public void logImportRun(UUID userId, String type, String filter) {
        try {
            dao.createLog(userId, type, filter);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** Full‐blown log entry (e.g. report export with metadata) */
    public ReportLog logReport(ReportLog rl) {
        try {
            return dao.create(rl);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** When was the last import of the given type? */
    public Timestamp getLastRun(String type) {
        try {
            return dao.findLastByType(type);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** List *all* report logs. */
    public List<ReportLog> listAllReportLogs() {
        try {
            return dao.findAll();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
