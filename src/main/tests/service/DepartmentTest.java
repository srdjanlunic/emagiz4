package service;

import dao.OrganizationDAO;
import java.util.UUID;
import model.Department;
import model.Organization;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;


/**
 * Tests the methods in the class DepartmentService
 */
public class DepartmentTest {
    
    private DepartmentService svc;
    private Organization organization;
    private Department department;
    
    /**
     * Create a department and organization to be used for tests
     */
    @BeforeEach
    public void setup() {
        svc = new DepartmentService();
        var organizationModel = new Organization("Department Test Org");
        organizationModel.setId(UUID.randomUUID());
        organization = new OrganizationDAO().create(organizationModel);
        var departmentModel = new Department("Unit Test Department", "Test", organization.getId());
        department = svc.createDepartment(departmentModel);
    }
    
    /**
     * Delete the department to avoid interfering with other tests
     */
    @AfterEach
    public void cleanup() {
        new OrganizationDAO().delete(organization.getId());
        svc.deleteDepartment(department.getId());
    }
    
    /**
     * Tests the isValidDepartment method. Tests if both valid
     * and invalid strings are properly identified.
     */
    @Test
    public void testIsValidDepartmentName() {
        assertTrue(svc.isValidDepartmentName("Valid Name"));
        assertTrue(svc.isValidDepartmentName("Valid_Name-2"));
        assertFalse(svc.isValidDepartmentName("@()%^$"));
    }
    
    /**
     * Tests the createDepartment method. Tests if a department is
     * correctly created, and if it matches the model used to create it.
     */
    @Test
    public void testCreateDepartment() {
        assertNotNull(department);
        
        var departmentModel = new Department("Unit Test Department", "Test", organization.getId());
        departmentModel.setId(department.getId());
        departmentModel.setCreatedAt(department.getCreatedAt());
        assertEquals(departmentModel, department);
    }
    
    /**
     * Tests the getAllDepartments method. Tests if queried departments list
     * contains the newly created department.
     */
    @Test
    public void testGetAllDepartments() {
        assertTrue(svc.getAllDepartments().contains(department));
    }
    
    /**
     * Tests the getDepartmentById method. Tests if the newly created
     * department is correctly returned by the query.
     */
    @Test
    public void testGetDepartmentById() {
        var queriedDepartment = svc.getDepartmentById(department.getId());
        
        assertEquals(department, queriedDepartment);
    }
    
    /**
     * Tests the updateDepartment method. Tests if the updated department
     * has the correct updated information from the model.
     */
    @Test
    public void testUpdateDepartment() {
        var updatedModel = new Department("Unit Test Department Update", "Update", organization.getId());
        updatedModel.setId(department.getId());
        updatedModel.setCreatedAt(department.getCreatedAt());
        
        var updatedDepartment = svc.updateDepartment(updatedModel);
        assertEquals(updatedModel, updatedDepartment);
    }
    
    /**
     * Tests the deleteDepartment method. Tests if the department is
     * correctly deleted and returns as true for confirming deletion.
     */
    @Test
    public void testDeleteDepartment() {
        var deleted = svc.deleteDepartment(department.getId());
        assertTrue(deleted);
    }
    
    /**
     * Tests the getDepartmentsByOrganization method. Tests if the
     * created department is contained in the list of departments in
     * the given organization.
     */
    @Test
    public void testGetDepartmentsByOrganization() {
        var departmentsInOrganization = svc.getDepartmentsByOrganization(organization.getId());
        assertTrue(departmentsInOrganization.contains(department));
    }
}
