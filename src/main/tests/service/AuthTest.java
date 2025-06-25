package service;

import dao.UserDAO;
import java.util.UUID;
import model.User;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class AuthTest {
    private AuthService authService;
    private UserDAO userDAO;
    private final String plainPassword = "securePassword";
    
    private UUID testUserId;
    private String username;
    private String email;
    private String hashedPassword;
    
    @BeforeEach
    public void setup() {
        authService = new AuthService();
        userDAO = new UserDAO();
        
        // Generate unique username and email to avoid conflicts
        long timestamp = System.currentTimeMillis();
        username = "test_user_" + timestamp;
        email = "test_" + timestamp + "@example.com";
        
        // Clean up any existing user with this username first
        
        
        // Create a test user in the database
        User user = new User();
        testUserId = UUID.randomUUID();
        user.setId(testUserId);
        user.setUsername(username);
        
        
        hashedPassword = authService.hashPassword(plainPassword);
        user.setPassword(hashedPassword);
        user.setEmail(email);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setActive(true);
        
        try {
            userDAO.create(user);
        } catch (Exception e) {
            System.err.println("Failed to create test user: " + e.getMessage());
            throw new RuntimeException("Test setup failed", e);
        }
    }
    
    
    
    @Test
    public void testAuthenticate_ValidCredentials_ReturnsUser() {
        // Debug: Verify user was created
        User createdUser = userDAO.findByUsername(username);
        assertNotNull(createdUser, "Test user should exist in database");
        User result = authService.authenticate(username, plainPassword);
        
        assertNotNull(result, "Expected a non-null user object for valid credentials");
        assertEquals(username, result.getUsername(), "Username mismatch");
        assertEquals(testUserId, result.getId(), "User ID mismatch");
        assertTrue(result.isActive(), "User should be active");
    }
    
    @Test
    public void testAuthenticate_InvalidUsername_ReturnsNull() {
        User result = authService.authenticate("nonexistent_user", plainPassword);
        assertNull(result, "Expected null for invalid username");
    }
    
    @Test
    public void testAuthenticate_InvalidPassword_ReturnsNull() {
        User result = authService.authenticate(username, "wrongPassword");
        assertNull(result, "Expected null for invalid password");
    }
    
    @Test
    public void testAuthenticate_InactiveUser_ReturnsNull() {
        // Create an inactive user
        User inactiveUser = new User();
        UUID inactiveUserId = UUID.randomUUID();
        inactiveUser.setId(inactiveUserId);
        inactiveUser.setUsername("inactive_user");
        inactiveUser.setPassword(hashedPassword);
        inactiveUser.setEmail("inactive@test.com");
        inactiveUser.setFirstName("Inactive");
        inactiveUser.setLastName("User");
        inactiveUser.setActive(false);
        
        userDAO.create(inactiveUser);
        
        try {
            User result = authService.authenticate("inactive_user", plainPassword);
            assertNull(result, "Expected null for inactive user");
        } finally {
            // Clean up the inactive user
            userDAO.delete(inactiveUserId);
        }
    }
    
    @Test
    public void testAuthenticate_NullCredentials_ReturnsNull() {
        User result1 = authService.authenticate(null, plainPassword);
        User result2 = authService.authenticate(username, null);
        User result3 = authService.authenticate(null, null);
        
        assertNull(result1, "Expected null for null username");
        assertNull(result2, "Expected null for null password");
        assertNull(result3, "Expected null for null credentials");
    }
    
    @AfterEach
    public void cleanup() {
        // Clean up the test user
        if (testUserId != null) {
            try {
                userDAO.delete(testUserId);
            } catch (Exception e) {
                System.err.println("Failed to cleanup test user: " + e.getMessage());
            }
        }
        
        // Also try cleanup by username as backup
        if (username != null) {
            try {
                User existingUser = userDAO.findByUsername(username);
                if (existingUser != null) {
                    userDAO.delete(existingUser.getId());
                }
            } catch (Exception e) {
            
            }
        }
    }
}

