package service;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class DepartmentTest {
    
    private DepartmentService depService;
    
    @BeforeEach
    public void setup() {
        depService = new DepartmentService();
    }
    
    @Test
    public void testIsValidDepartmentName() {
        assertTrue(depService.isValidDepartmentName("Valid Name"));
        assertTrue(depService.isValidDepartmentName("Valid_Name-2"));
        assertFalse(depService.isValidDepartmentName("@()%^$"));
    }
}
