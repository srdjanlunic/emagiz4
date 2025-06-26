package service;

import dao.UserDAO;
import java.util.UUID;
import model.User;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the methods in the class AuthService.
 */
public class AuthTest {
    private AuthService svc;
    private UserDAO userDAO;
    private final String plainPassword = "securePassword";
    
    private UUID testUserId;
    private String username;
    private String email;
    private String hashedPassword;
    
    /**
     * Create a test user with a unique username and email to be used for authentication tests.
     */
    @BeforeEach
    public void setup() {
        svc = new AuthService();
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
        
        
        hashedPassword = svc.hashPassword(plainPassword);
        user.setPassword(hashedPassword);
        user.setEmail(email);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setActive(true);
        
        try {
            userDAO.create(user);
        } catch (Exception e) {
            System.err.println("Failed to create test user: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Remove the test user from the database after each test.
     */
    @AfterEach
    public void cleanup() {
        userDAO.delete(testUserId);
    }
    
    /**
     * Test the password hashing functionality to ensure the plain password is transformed.
     */
    @Test
    public void testHashPassword() {
        String hash = svc.hashPassword(plainPassword);
        assertNotEquals(plainPassword, hash);
    }
    
    /**
     * Test the password verification logic using both correct and incorrect passwords.
     */
    @Test
    public void testVerifyPassword() {
        assertTrue(svc.verifyPassword(plainPassword, hashedPassword));
        assertFalse(svc.verifyPassword("wrongPassword", hashedPassword));
    }
    
    /**
     * Test the authentication method with valid and invalid credentials.
     */
    @Test
    public void testAuthenticate() {
        // Correct username and password should authenticate successfully
        User user = svc.authenticate(username, plainPassword);
        assertNotNull(user);
        assertEquals(username, user.getUsername());
        
        // Wrong password should result in failed authentication
        User failedUser = svc.authenticate(username, "wrongPassword");
        assertNull(failedUser);
    }
}
