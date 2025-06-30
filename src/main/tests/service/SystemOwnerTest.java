package service;

import dao.SystemImplementationDAO;
import dao.UserDAO;
import dto.UserCreationRequestDto;
import model.SystemImplementation;
import model.User;
import org.junit.jupiter.api.*;
import service.SystemImplementationService;
import service.SystemOwnerService;
import service.UserService;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;



public class SystemOwnerTest {
    private UserService userService;
    private SystemImplementationService implService;
    private SystemOwnerService ownerService;
    
    private User testUser;
    private SystemImplementation testImplementation;
    
    @BeforeEach
    public void setup() {
        userService = new UserService();
        implService = new SystemImplementationService();
        ownerService = new SystemOwnerService();
        
        UserCreationRequestDto userDto = new UserCreationRequestDto();
        userDto.setUsername(UUID.randomUUID().toString());
        userDto.setEmail(UUID.randomUUID().toString());
        userDto.setPassword("owner123");
        userDto.setRole("system_owner");
        testUser = userService.createUser(userDto);
        
        // Create test system implementation manually
        SystemImplementation impl = new SystemImplementation();
        impl.setSystemId(UUID.randomUUID());
        impl.setVersion("1.0.0");
        impl.setEnvironment("Test");
        impl.setCriticalityLevel("Medium");
        impl.setDataClassification("Internal");
        impl.setRiskScore("10");
        impl.setSensitiveCustomerData(false);
        
        testImplementation = new SystemImplementationDAO().findAll().get(0); //new dao.SystemImplementationDAO().create(impl);
    }
    
    @AfterEach
    public void cleanup() {
        new UserDAO().delete(testUser.getId());
        new SystemImplementationDAO().delete(testImplementation.getId());
    }
    
    /**
     * Assign a user as owner and verify retrieval.
     */
    @Test
    public void testAssignAndGetOwners() {
        ownerService.assignOwner(testUser.getId(), testImplementation.getId());
        
        List<User> owners = ownerService.getOwnersForImplementation(testImplementation.getId());
        assertFalse(owners.isEmpty());
        assertEquals(testUser.getId(), owners.get(0).getId());
    }
    /**
     * Remove owner and check it no longer exists.
     */
    @Test
    public void testRemoveOwner() {
        ownerService.assignOwner(testUser.getId(), testImplementation.getId());
        
        boolean removed = ownerService.removeOwner(testUser.getId(), testImplementation.getId());
        assertTrue(removed);
        
        List<User> owners = ownerService.getOwnersForImplementation(testImplementation.getId());
        assertTrue(owners.isEmpty());
    }
    /**
     * Assign implementation and verify retrieval by user.
     */
    @Test
    public void testGetImplementationsForOwner() {
        ownerService.assignOwner(testUser.getId(), testImplementation.getId());
        
        List<SystemImplementation> impls = ownerService.getImplementationsForOwner(testUser.getId());
        assertFalse(impls.isEmpty());
        assertEquals(testImplementation.getId(), impls.get(0).getId());
    }
    /**
     * Clean up test data.
     */
    @AfterEach
    public void tearDown() {
        ownerService.removeOwner(testUser.getId(), testImplementation.getId());
    }
}

    
    
    

