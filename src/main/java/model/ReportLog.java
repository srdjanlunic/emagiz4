package model;

import java.sql.Timestamp;
import java.util.UUID;

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

    public ReportLog() {
        // default constructor
    }

    // convenience constructor for creation
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

    // getters & setters

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getGeneratedBy() { return generatedBy; }
    public void setGeneratedBy(UUID generatedBy) { this.generatedBy = generatedBy; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Timestamp getDateRangeStart() { return dateRangeStart; }
    public void setDateRangeStart(Timestamp dateRangeStart) { this.dateRangeStart = dateRangeStart; }

    public Timestamp getDateRangeEnd() { return dateRangeEnd; }
    public void setDateRangeEnd(Timestamp dateRangeEnd) { this.dateRangeEnd = dateRangeEnd; }

    public String getFilters() { return filters; }
    public void setFilters(String filters) { this.filters = filters; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getFileFormat() { return fileFormat; }
    public void setFileFormat(String fileFormat) { this.fileFormat = fileFormat; }

    public Timestamp getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(Timestamp generatedAt) { this.generatedAt = generatedAt; }
}
