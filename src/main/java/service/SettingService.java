package service;

import dao.SettingDAO;

public class SettingService {
    private SettingDAO settingDAO;

    public SettingService() {
        this.settingDAO = new SettingDAO();
    }

    public String getSetting(String key) {
        return settingDAO.getSetting(key);
    }

    public void saveSetting(String key, String value) {
        settingDAO.saveSetting(key, value);
    }
} 