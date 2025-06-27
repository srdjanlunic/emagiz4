package service;
import java.sql.Timestamp;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class SettingTest {
    
    private SettingService svc;
    
    @Test
    public void testSaveAndGetSetting() {
        svc = new SettingService();
        
        var key = "last_cve_import_time";
        var value = new Timestamp(System.currentTimeMillis()).toString();
        
        svc.saveSetting(key, value);
        assertEquals(value, svc.getSetting(key));
    }
}
