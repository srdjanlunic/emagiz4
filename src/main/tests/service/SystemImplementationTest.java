package service;
import dao.DepartmentDAO;
import dao.OrganizationDAO;
import dao.SystemDAO;
import dto.SystemDto;
import dto.SystemImplementationDto;
import java.sql.Timestamp;
import java.util.UUID;
import model.Department;
import model.ITSystem;
import model.Organization;
import model.SystemImplementation;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class SystemImplementationTest {
    
    private SystemImplementationService svc;
    private SystemImplementationDto implem;
    private ITSystem system;
    private Organization organization;
    private Department department;
    
    @BeforeEach
    public void setup() {
        svc = new SystemImplementationService();
        
        var sysModel = new ITSystem("SysImplm Test", "Test", "Test");
        system = new SystemDAO().create(sysModel);
        var orgModel = new Organization("SysImplm Test");
        organization = new OrganizationDAO().create(orgModel);
        var depModel = new Department("SysImplm Test", "Test", organization.getId());
        department = new DepartmentDAO().create(depModel);
        
        var implemModel = new SystemImplementationDto
                (UUID.randomUUID(), system.getId(), department.getId(), "INTERNAL",
                 "MEDIUM", false, false, 0,
                 "1.7", "Internal", new Timestamp(System.currentTimeMillis()),
                 new Timestamp(System.currentTimeMillis()));
        implem = svc.createSystemImplementation(implemModel);
    }
    
    @AfterEach
    public void cleanup() {
        new SystemDAO().delete(system.getId());
        new OrganizationDAO().delete(organization.getId());
        new DepartmentDAO().delete(department.getId());
        svc.deleteSystemImplementation(implem.getId());
    }
    
    @Test
    public void testCreateSystemImplementation() {
        assertEquals(system.getId(), implem.getSystemId());
        assertEquals(department.getId(), implem.getDepartmentId());
        assertEquals("INTERNAL", implem.getDataClassification());
        assertEquals("MEDIUM", implem.getCriticalityLevel());
        assertEquals(0, implem.getRiskScore());
        assertEquals("Internal", implem.getEnvironment());
    }
    
    @Test
    public void testGetAllSystemImplementations() {
        var implemList = svc.getAllSystemImplementations();
        assertTrue(implemList.contains(implem));
    }
    
    @Test
    public void testGetSystemImplementationById() {
        assertEquals(implem, svc.getSystemImplementationById(implem.getId()));
    }
    
    @Test
    public void testUpdateSystemImplementation() {
        var id = implem.getId();
        var updatedModel = new SystemImplementationDto
                (id, system.getId(), department.getId(), "INTERNAL",
                 "MEDIUM", false, false, 0,
                 "1.8", "Production", new Timestamp(System.currentTimeMillis()),
                 new Timestamp(System.currentTimeMillis()));
        var updatedImplem = svc.updateSystemImplementation(implem.getId(), updatedModel);
        assertEquals(updatedImplem, updatedModel);
    }
    
    @Test
    public void testDeleteSystemImplementation() {
        assertTrue(svc.deleteSystemImplementation(implem.getId()));
    }
    
    @Test
    public void testCalculateRiskScore() {
        var sysImplem = new SystemImplementation();
        sysImplem.setId(implem.getId());
        sysImplem.setSystemId(implem.getSystemId());
        sysImplem.setDepartmentId(implem.getDepartmentId());
        sysImplem.setDataClassification(implem.getDataClassification());
        sysImplem.setCriticalityLevel(implem.getCriticalityLevel());
        sysImplem.setInternetFacing(implem.isInternetFacing());
        sysImplem.setSensitiveCustomerData(implem.isSensitiveCustomerData());
        sysImplem.setRiskScore(implem.getRiskScore());
        sysImplem.setVersion(implem.getVersion());
        sysImplem.setEnvironment(implem.getEnvironment());
        sysImplem.setCreatedAt(implem.getCreatedAt());
        sysImplem.setUpdatedAt(implem.getUpdatedAt());
        
        assertEquals(30, svc.calculateRiskScore(sysImplem));
    }
    
    @Test
    public void testGetImplementationsByDepartment() {
        var implemList = svc.getImplementationsByDepartment(department.getId());
        assertTrue(implemList.contains(implem));
    }
    
    @Test
    public void testGetImplementationsBySystem() {
        var implemList = svc.getImplementationsBySystem(system.getId());
        assertTrue(implemList.contains(implem));
    }
}
