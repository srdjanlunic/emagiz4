package service;

import dao.UserDAO;
import java.sql.Timestamp;
import java.util.UUID;
import model.ReportLog;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class ReportLogTest {
    
    private ReportLogService svc;
    private ReportLog reportLog;
    private UUID randomUserId;
    
    @BeforeEach
    public void setup() {
        svc = new ReportLogService();
        randomUserId = new UserDAO().findAll().get(0).getId();
        var reportLogModel = new ReportLog(randomUserId, "Test", "Test Log",
                                           new Timestamp(System.currentTimeMillis()-100),
                                           new Timestamp(System.currentTimeMillis()+100),
                                           "", "/reportlogs", ".pdf");
        
        reportLog = svc.logReport(reportLogModel);
    }
    
    @Test
    public void testLogReport() {
        assertEquals(randomUserId, reportLog.getGeneratedBy());
        assertEquals("Test", reportLog.getType());
        assertEquals("Test Log", reportLog.getTitle());
        assertEquals(new Timestamp(System.currentTimeMillis()-100), reportLog.getDateRangeStart());
        assertEquals(new Timestamp(System.currentTimeMillis()+100), reportLog.getDateRangeEnd());
        assertNull(reportLog.getFilters());
        assertEquals("/reportlogs", reportLog.getFilePath());
        assertEquals(".pdf", reportLog.getFileFormat());
    }
    
    @Test
    public void testGetLastRun() {
        var lastRunTime = svc.getLastRun("Test").getTime();
        assertTrue(
                lastRunTime >= System.currentTimeMillis()-100
                        && lastRunTime <= System.currentTimeMillis());
    }
    
    @Test
    public void testListAllReportLogs() {
        var logsList = svc.listAllReportLogs();
        assertTrue(logsList.contains(reportLog));
    }
}
