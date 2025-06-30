package dao;

import config.DatabaseConfig;
import model.AssessmentHistory;
import model.AssessmentStatus;
import util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Handles database operations for assessment history records.
 */
public class AssessmentHistoryDAO {
    
    /**
     * Adds a new assessment history entry to the database.
     *
     * @param history the history to save
     * @return the saved object with ID set, or null if failed
     */
    public AssessmentHistory create(AssessmentHistory history) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(
                    "INSERT INTO AssessmentHistory (id, assessment_id, changed_by, old_status, " +
                            "new_status, change_reason, changed_at) VALUES (?, ?, ?, ?::assessment_status, ?::assessment_status, ?, ?)"
            );
            
            history.setId(UUID.randomUUID());
            // Set query parameters
            stmt.setObject(1, history.getId());
            stmt.setObject(2, history.getAssessmentId());
            stmt.setObject(3, history.getChangedBy());
            stmt.setString(4, history.getOldStatus() != null ? history.getOldStatus().getValue() : null);
            stmt.setString(5, history.getNewStatus().getValue());
            stmt.setString(6, history.getChangeReason());
            stmt.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
            
            // Execute and return ID
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                return history;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, null);
        }
        return null;
    }
    
    /**
     * Finds a history entry by its ID.
     *
     * @param id the history ID
     * @return the found record or null
     */
    public AssessmentHistory findById(UUID id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM AssessmentHistory WHERE id = ?");
            stmt.setObject(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToAssessmentHistory(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, rs);
        }
        return null;
    }
    
    /**
     * Gets all history records for a given assessment ID.
     *
     * @param assessmentId the ID of the assessment
     * @return list of matching history entries
     */
    public List<AssessmentHistory> findByAssessment(UUID assessmentId) {
        List<AssessmentHistory> histories = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM AssessmentHistory WHERE assessment_id = ? ORDER BY changed_at DESC");
            stmt.setObject(1, assessmentId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                histories.add(mapResultSetToAssessmentHistory(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, rs);
        }
        return histories;
    }
    
    /**
     * Gets all assessment history records.
     *
     * @return list of all history entries
     */
    public List<AssessmentHistory> findAll() {
        List<AssessmentHistory> histories = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM AssessmentHistory ORDER BY changed_at DESC");
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                histories.add(mapResultSetToAssessmentHistory(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, rs);
        }
        return histories;
    }
    
    /**
     * Deletes a history record by ID.
     *
     * @param id the history record ID
     * @return true if deleted, false otherwise
     */
    public boolean delete(UUID id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement("DELETE FROM AssessmentHistory WHERE id = ?");
            stmt.setObject(1, id);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, null);
        }
        return false;
    }
    
    /**
     * Converts a row from the result set into an AssessmentHistory object.
     *
     * @param rs the SQL result set
     * @return mapped AssessmentHistory object
     * @throws SQLException if a column read fails
     */
    private AssessmentHistory mapResultSetToAssessmentHistory(ResultSet rs) throws SQLException {
        AssessmentHistory history = new AssessmentHistory();
        history.setId((UUID) rs.getObject("id"));
        history.setAssessmentId((UUID) rs.getObject("assessment_id"));
        history.setChangedBy((UUID) rs.getObject("changed_by"));
        
        // Handle nullable old_status
        String oldStatusStr = rs.getString("old_status");
        if (oldStatusStr != null) {
            history.setOldStatus(AssessmentStatus.fromValue(oldStatusStr));
        }
        
        history.setNewStatus(AssessmentStatus.fromValue(rs.getString("new_status")));
        history.setChangeReason(rs.getString("change_reason"));
        history.setChangedAt(rs.getTimestamp("changed_at"));
        return history;
    }
}
