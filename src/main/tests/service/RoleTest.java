package service;
import java.util.UUID;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class RoleTest {
    
    private RoleService svc;
    
    @BeforeEach
    public void setup() {
        svc = new RoleService();
    }
    
    @Test
    public void testGetRoleByName() {
        assertEquals("ADMIN", svc.getRoleByName("ADMIN").getName());
        assertEquals("SYSTEM_OWNER", svc.getRoleByName("SYSTEM_OWNER").getName());
        assertEquals("SECURITY_OFFICER", svc.getRoleByName("SECURITY_OFFICER").getName());
        assertEquals("TECHNICAL_EXPERT", svc.getRoleByName("TECHNICAL_EXPERT").getName());
    }
    
    @Test
    public void testGetRoleById() {
        var sysOwnerId = UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12");
        var secOfficerId = UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13");
        var techExpertId = UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14");
        var adminId = UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15");
        
        assertEquals(sysOwnerId, svc.getRoleById(sysOwnerId).getId());
        assertEquals(secOfficerId, svc.getRoleById(secOfficerId).getId());
        assertEquals(techExpertId, svc.getRoleById(techExpertId).getId());
        assertEquals(adminId, svc.getRoleById(adminId).getId());
    }
}