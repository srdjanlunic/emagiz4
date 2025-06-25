package model;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * Represents a log entry for a generated report.
 * Stores metadata like title, file location, filters, and generation details.
 */
public class ReportLog {
    private UUID id;
    private UUID generatedBy;
    private String type;
    private String title;
    private Timestamp dateRangeStart;
    private Timestamp dateRangeEnd;
    private String filters;
    private String filePath;
    private String fileFormat;
    private Timestamp generatedAt;
    
    /**
     * Default constructor.
     */
    public ReportLog() {
        // default constructor
    }
    
    /**
     * Creates a new report log with the provided data and sets the generatedAt timestamp.
     *
     * @param generatedBy      the user who generated the report
     * @param type             the type of the report
     * @param title            the title of the report
     * @param dateRangeStart   the start of the date range covered by the report
     * @param dateRangeEnd     the end of the date range covered by the report
     * @param filters          applied filters in the report
     * @param filePath         where the file is stored
     * @param fileFormat       format of the file (e.g., PDF, CSV)
     */
    public ReportLog(UUID generatedBy, String type, String title,
                     Timestamp dateRangeStart, Timestamp dateRangeEnd,
                     String filters, String filePath, String fileFormat) {
        this.generatedBy = generatedBy;
        this.type = type;
        this.title = title;
        this.dateRangeStart = dateRangeStart;
        this.dateRangeEnd = dateRangeEnd;
        this.filters = filters;
        this.filePath = filePath;
        this.fileFormat = fileFormat;
        this.generatedAt = new Timestamp(System.currentTimeMillis());
    }
    
    /**
     * @return the ID of the report log
     */
    public UUID getId() { return id; }
    
    /**
     * @param id sets the report log ID
     */
    public void setId(UUID id) { this.id = id; }
    
    /**
     * @return the ID of the user who generated the report
     */
    public UUID getGeneratedBy() { return generatedBy; }
    
    /**
     * @param generatedBy the user who generated the report
     */
    public void setGeneratedBy(UUID generatedBy) { this.generatedBy = generatedBy; }
    
    /**
     * @return the type of report
     */
    public String getType() { return type; }
    
    /**
     * @param type the report type
     */
    public void setType(String type) { this.type = type; }
    
    /**
     * @return the title of the report
     */
    public String getTitle() { return title; }
    
    /**
     * @param title the title of the report
     */
    public void setTitle(String title) { this.title = title; }
    
    /**
     * @return the start of the date range the report covers
     */
    public Timestamp getDateRangeStart() { return dateRangeStart; }
    
    /**
     * @param dateRangeStart start of the report's date range
     */
    public void setDateRangeStart(Timestamp dateRangeStart) { this.dateRangeStart = dateRangeStart; }
    
    /**
     * @return the end of the date range the report covers
     */
    public Timestamp getDateRangeEnd() { return dateRangeEnd; }
    
    /**
     * @param dateRangeEnd end of the report's date range
     */
    public void setDateRangeEnd(Timestamp dateRangeEnd) { this.dateRangeEnd = dateRangeEnd; }
    
    /**
     * @return filters applied to the report
     */
    public String getFilters() { return filters; }
    
    /**
     * @param filters filters applied to the report
     */
    public void setFilters(String filters) { this.filters = filters; }
    
    /**
     * @return path to the generated report file
     */
    public String getFilePath() { return filePath; }
    
    /**
     * @param filePath path to the generated file
     */
    public void setFilePath(String filePath) { this.filePath = filePath; }
    
    /**
     * @return format of the generated file
     */
    public String getFileFormat() { return fileFormat; }
    
    /**
     * @param fileFormat format of the file (e.g., PDF)
     */
    public void setFileFormat(String fileFormat) { this.fileFormat = fileFormat; }
    
    /**
     * @return timestamp when the report was generated
     */
    public Timestamp getGeneratedAt() { return generatedAt; }
    
    /**
     * @param generatedAt timestamp when the report was generated
     */
    public void setGeneratedAt(Timestamp generatedAt) { this.generatedAt = generatedAt; }
}
