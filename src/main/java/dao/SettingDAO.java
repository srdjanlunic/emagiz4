package dao;

import config.DatabaseConfig;
import util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Provides access to application settings stored in the database.
 */
public class SettingDAO {
    
    /**
     * Retrieves a setting value by its key.
     *
     * @param key the setting name
     * @return the setting value, or null if not found
     */
    public String getSetting(String key) {
        String sql = "SELECT value FROM settings WHERE key = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, key);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("value");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Saves or updates a setting in the database.
     * If the key already exists, it updates the value.
     *
     * @param key the setting name
     * @param value the setting value
     */
    public void saveSetting(String key, String value) {
        String sql = "INSERT INTO settings (key, value) VALUES (?, ?) ON CONFLICT (key) DO UPDATE SET value = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, key);
            stmt.setString(2, value);
            stmt.setString(3, value); // used in case of update
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
