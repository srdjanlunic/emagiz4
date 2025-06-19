package dao;

import config.DatabaseConfig;
import model.ReportLog;
import util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReportLogDAO {
    public void createLog(UUID userId, String type, String filter) throws SQLException {
        String sql = "INSERT INTO ReportLog (generated_by, type, filters, generated_at) VALUES (?, ?, ?, now())";
        DatabaseUtil.executeUpdate(sql, userId, type, filter);
    }

    /** insert a full ReportLog record */
    public ReportLog create(ReportLog rl) throws SQLException {
        String sql =
                "INSERT INTO reportlog (" +
                        "  id, generated_by, type, title, date_range_start, date_range_end, filters, file_path, file_format, generated_at" +
                        ") VALUES (?,?,?,?,?,?,?,?,?,?) RETURNING id, generated_at";
        try (
                Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setObject(1, UUID.randomUUID());
            ps.setObject(2, rl.getGeneratedBy());
            ps.setString( 3, rl.getType());
            ps.setString( 4, rl.getTitle());
            ps.setTimestamp(5, rl.getDateRangeStart());
            ps.setTimestamp(6, rl.getDateRangeEnd());
            ps.setString( 7, rl.getFilters());
            ps.setString( 8, rl.getFilePath());
            ps.setString( 9, rl.getFileFormat());
            ps.setTimestamp(10, rl.getGeneratedAt());
            var rs = ps.executeQuery();
            if (rs.next()) {
                rl.setId((UUID) rs.getObject("id"));
                rl.setGeneratedAt(rs.getTimestamp("generated_at"));
                return rl;
            }
            return null;
        }
    }

    /** find the most recent timestamp for a given type */
    public Timestamp findLastByType(String type) throws SQLException {
        String sql = "SELECT generated_at FROM reportlog WHERE type = ? ORDER BY generated_at DESC LIMIT 1";
        var rs = DatabaseUtil.executeQuery(sql, type);
        if (rs.next()) return rs.getTimestamp("generated_at");
        return null;
    }

    /** list *all* report‐log entries (for audit) */
    public List<ReportLog> findAll() throws SQLException {
        String sql = "SELECT * FROM reportlog ORDER BY generated_at DESC";
        List<ReportLog> out = new ArrayList<>();
        try (
                Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                ReportLog rl = new ReportLog();
                rl.setId((UUID) rs.getObject("id"));
                rl.setGeneratedBy((UUID) rs.getObject("generated_by"));
                rl.setType(rs.getString("type"));
                rl.setTitle(rs.getString("title"));
                rl.setDateRangeStart(rs.getTimestamp("date_range_start"));
                rl.setDateRangeEnd(rs.getTimestamp("date_range_end"));
                rl.setFilters(rs.getString("filters"));
                rl.setFilePath(rs.getString("file_path"));
                rl.setFileFormat(rs.getString("file_format"));
                rl.setGeneratedAt(rs.getTimestamp("generated_at"));
                out.add(rl);
            }
        }
        return out;
    }
}
