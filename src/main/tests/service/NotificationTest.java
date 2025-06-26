package service;
import dto.NotificationDto;
import dto.UserCreationRequestDto;
import model.User;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.UUID;
import model.Notification;




/**
 * Tests the methods in the class NotificationService.
 */
public class NotificationTest {
    
    private NotificationService svc;
    private NotificationDto notification;
    private User user;
    
    /**
     * Create a user and notification to be used in tests.
     */
    @BeforeEach
    public void setup() {
        svc = new NotificationService();
        
        // Set up a test user using the UserCreationRequestDto
        var userModel = new UserCreationRequestDto();
        userModel.setUsername("Notification Test User");
        userModel.setEmail("notiftest@gmail.com");
        userModel.setPassword("notiftest123");
        userModel.setRole("security_officer");
        
        // Create the user and notification
        user = new UserService().createUser(userModel);
        notification = svc.createNotification(user.getId(), "Notification", "Test", null);
    }
    
    /**
     * Mark the created notification as read after each test to avoid side effects.
     */
    @AfterEach
    public void cleanup() {
        svc.markNotificationAsRead(notification.getId());
    }
    
    /**
     * Test the createNotification method to ensure all fields are correctly set.
     */
    @Test
    public void testCreateNotification() {
        assertNotNull(notification);
        assertEquals(user.getId(), notification.getUserId());
        assertEquals("Notification", notification.getType());
        assertEquals("Test", notification.getMessage());
    }
    
    /**
     * Test retrieving notifications for the user, including after marking as read.
     */
    @Test
    public void testGetNotificationsForUser() {
        var notifsForUser = svc.getNotificationsForUser(user.getId());
        assertTrue(notifsForUser.contains(notification));
        
        // Mark the notification as read and verify it's still retrievable
        svc.markNotificationAsRead(notification.getId());
        notifsForUser = svc.getNotificationsForUser(user.getId());
        assertTrue(notifsForUser.contains(notification));
    }
    
    /**
     * Test retrieving only unread notifications before and after marking as read.
     */
    @Test
    public void testGetUnreadNotificationsForUser() {
        var unreadNotifsForUser = svc.getNotificationsForUser(user.getId());
        assertTrue(unreadNotifsForUser.contains(notification));
        
        svc.markNotificationAsRead(notification.getId());
        unreadNotifsForUser = svc.getNotificationsForUser(user.getId());
        assertFalse(unreadNotifsForUser.contains(notification));
    }
    
    /**
     * Test the behavior of marking a notification as read twice.
     */
    @Test
    public void testMarkNotificationAsRead() {
        assertTrue(svc.markNotificationAsRead(notification.getId()));
        assertFalse(svc.markNotificationAsRead(notification.getId()));
    }
    
    /**
     * Placeholder test for creating vulnerability match notifications.
     */
    @Test
    public void testCreateVulnerabilityMatchNotifications() {
        try {
            svc.createVulnerabilityMatchNotifications(
                    UUID.randomUUID(),
                    "CVE-1234-5678",
                    List.of(UUID.randomUUID(), UUID.randomUUID())
            );
        } catch (Exception e) {
            fail("Should not throw exception in placeholder method");
        }
    }
    
    /**
     * Placeholder test for sending notifications.
     */
    @Test
    public void testSendNotifications() {
        Notification n = new Notification();
        n.setUserId(user.getId());
        n.setMessage("Simulated send");
        
        assertDoesNotThrow(() -> svc.sendNotification(n));
    }
}
