package dto;

import java.util.UUID;

/**
 * Data Transfer Object for user information with role and department details.
 */
public class UserDto {
    private UUID id;
    private String username;
    private String email;
    private String roleName;
    private String departmentName;
    
    /**
     * Constructs a UserDto with the given attributes.
     *
     * @param id              unique identifier of the user
     * @param username        user's username
     * @param email           user's email
     * @param roleName        name of the user's role
     * @param departmentName  name of the user's department
     */
    public UserDto(UUID id, String username, String email, String roleName, String departmentName) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.roleName = roleName;
        this.departmentName = departmentName;
    }
    
    /**
     * Gets the user ID.
     *
     * @return user ID
     */
    public UUID getId() {
        return id;
    }
    
    /**
     * Sets the user ID.
     *
     * @param id user ID
     */
    public void setId(UUID id) {
        this.id = id;
    }
    
    /**
     * Gets the username.
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Sets the username.
     *
     * @param username username
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * Gets the email address.
     *
     * @return email
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Sets the email address.
     *
     * @param email email address
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * Gets the role name.
     *
     * @return role name
     */
    public String getRoleName() {
        return roleName;
    }
    
    /**
     * Sets the role name.
     *
     * @param roleName role name
     */
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
    
    /**
     * Gets the department name.
     *
     * @return department name
     */
    public String getDepartmentName() {
        return departmentName;
    }
    
    /**
     * Sets the department name.
     *
     * @param departmentName department name
     */
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
}
