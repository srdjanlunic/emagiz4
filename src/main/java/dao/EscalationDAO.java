package dao;

import config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import model.Escalation;
import model.EscalationStatus;
import util.DatabaseUtil;
import java.sql.Statement;
import java.sql.Types;

public class EscalationDAO {
    
    public Escalation create(Escalation escalation) {
        String sql = "INSERT INTO escalation (id, system_vulnerability_id," +
                "security_officer_id, escalation_reason, escalation_date, escalation_status," +
                "tech_expert_id, response, response_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            escalation.setId(UUID.randomUUID()); // Generate UUID manually
            stmt.setObject(1, escalation.getId());
            stmt.setObject(2, escalation.getSystemVulnerabilityId());
            stmt.setObject(3, escalation.getSecurityOfficerId());
            stmt.setString(4, escalation.getEscalationReason());
            stmt.setTimestamp(5, escalation.getEscalationDate());
            stmt.setString(6, escalation.getEscalationStatus().getValue());
            if (escalation.getTechExpertId() == null) {
                stmt.setNull(7, Types.OTHER);
            } else {
                stmt.setObject(7, escalation.getTechExpertId());
            }
            stmt.setString(8, escalation.getResponse());
            stmt.setTimestamp(9, escalation.getResponseDate());
            
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                return null;
            }

            return escalation;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public Escalation update(Escalation escalation) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(
                    "UPDATE escalation SET system_vulnerability_id = ?, security_officer_id = ?, " +
                            "escalation_reason = ?, escalation_date = ?, escalation_status = ?, " +
                            "tech_expert_id = ?, response = ?, response_date = ? WHERE id = ?"
            );
            
            stmt.setObject(1, escalation.getSystemVulnerabilityId());
            stmt.setObject(2, escalation.getSecurityOfficerId());
            stmt.setString(3, escalation.getEscalationReason());
            stmt.setTimestamp(4, escalation.getEscalationDate());
            stmt.setString(5, escalation.getEscalationStatus().getValue());
            stmt.setObject(6, escalation.getTechExpertId());
            stmt.setString(7, escalation.getResponse());
            stmt.setTimestamp(8, escalation.getResponseDate());
            stmt.setObject(9, escalation.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                return escalation;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, null);
        }
        return null;
    }
    
    public boolean delete(UUID id) {
        String sql = "DELETE FROM escalation WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setObject(1, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public Escalation findById(UUID id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM escalation WHERE id = ?");
            stmt.setObject(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToEscalation(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, rs);
        }
        return null;
    }
    
    public Escalation findBySystemVulnerabilityId(UUID systemVulnerabilityId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM escalation WHERE system_vulnerability_id = ?");
            stmt.setObject(1, systemVulnerabilityId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToEscalation(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, rs);
        }
        return null;
    }
    
    public List<Escalation> findBySecurityOfficer(UUID securityOfficerId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        var escalations = new ArrayList<Escalation>();
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM escalation WHERE security_officer_id = ?");
            stmt.setObject(1, securityOfficerId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                escalations.add(mapResultSetToEscalation(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, rs);
        }
        return escalations;
    }
    
    public List<Escalation> findByTechExpert(UUID techExpertId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        var escalations = new ArrayList<Escalation>();
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM escalation WHERE tech_expert_id = ?");
            stmt.setObject(1, techExpertId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                escalations.add(mapResultSetToEscalation(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, rs);
        }
        return escalations;
    }

    public List<Escalation> findByTechExpertOrUnassigned(UUID techExpertId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        var escalations = new ArrayList<Escalation>();
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM escalation WHERE tech_expert_id = ? OR tech_expert_id IS NULL");
            stmt.setObject(1, techExpertId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                escalations.add(mapResultSetToEscalation(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(conn, stmt, rs);
        }
        return escalations;
    }
    
    public List<Escalation> findAll() {
        String sql = "SELECT * FROM escalation";
        List<Escalation> escalations = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                escalations.add(mapResultSetToEscalation(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return escalations;
    }
    
    private Escalation mapResultSetToEscalation(ResultSet rs) throws SQLException {
        Escalation escalation = new Escalation();
        escalation.setId(rs.getObject("id", UUID.class));
        escalation.setSystemVulnerabilityId((UUID) rs.getObject("system_vulnerability_id"));
        escalation.setSecurityOfficerId((UUID) rs.getObject("security_officer_id"));
        escalation.setEscalationReason(rs.getString("escalation_reason"));
        escalation.setEscalationDate(rs.getTimestamp("escalation_date"));
        escalation.setEscalationStatus(EscalationStatus.fromValue(rs.getString("escalation_status")));
        escalation.setTechExpertId((UUID) rs.getObject("tech_expert_id"));
        escalation.setResponse(rs.getString("response"));
        escalation.setResponseDate(rs.getTimestamp("response_date"));
        return escalation;
    }
}
