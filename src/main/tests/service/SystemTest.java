package service;
import dto.SystemDto;
import dto.SystemRegistrationDto;
import model.SystemImplementation;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class SystemTest {
    
    private SystemService svc;
    private SystemImplementation system;
    
    @BeforeEach
    public void setup() {
        svc = new SystemService();
        var systemModel = new SystemRegistrationDto();
        systemModel.setName("Unit Testing System");
        systemModel.setVendor("Tester");
        systemModel.setDescription("Test");
        systemModel.setVersion("1.7");
        systemModel.setInternetFacing(false);
        systemModel.setDataClassification("INTERNAL");
        systemModel.setCriticalityLevel("MEDIUM");
        
        
        system = svc.registerSystem(systemModel);
    }
    
    @AfterEach
    public void cleanup() {
        svc.deleteSystem(system.getId());
    }
    
    @Test
    public void testRegisterSystem() {
        assertNotNull(system);
    }
    
    @Test
    public void testGetAllSystems() {
        assertNotNull(svc.getAllSystems());
    }
    
    @Test
    public void testGetSystemById() {
        System.out.println(svc.getSystemById(system.getId()));
        //assertEquals(system.getSystemId(), svc.getSystemById(system.getId()).getId());
    }
    
    @Test
    public void testUpdateSystem() {
        var systemModel = new SystemDto();
    }
    
    @Test
    public void deleteSystem() {
    
    }
}
