package service;

import dao.SettingDAO;

/**
 * Service class for managing application settings.
 */
public class SettingService {
    private SettingDAO settingDAO;
    
    /**
     * Constructs a new SettingService with a SettingDAO instance.
     */
    public SettingService() {
        this.settingDAO = new SettingDAO();
    }
    
    /**
     * Retrieves the value of a setting by its key.
     *
     * @param key the key of the setting to retrieve
     * @return the value of the setting, or null if not found
     */
    public String getSetting(String key) {
        return settingDAO.getSetting(key);
    }
    
    /**
     * Saves or updates a setting with the given key and value.
     *
     * @param key the key of the setting to save
     * @param value the value to associate with the key
     */
    public void saveSetting(String key, String value) {
        settingDAO.saveSetting(key, value);
    }
}
