package dto;

/**
 * DTO for creating a new user with role and department.
 */
public class UserCreationRequestDto {
    private String username;
    private String email;
    private String password;
    private String role;
    private String department;
    
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
     * @param username new username
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
     * @param email new email
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * Gets the password.
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * Sets the password.
     *
     * @param password new password
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     * Gets the user's role.
     *
     * @return role name
     */
    public String getRole() {
        return role;
    }
    
    /**
     * Sets the user's role.
     *
     * @param role role name
     */
    public void setRole(String role) {
        this.role = role;
    }
    
    /**
     * Gets the department name.
     *
     * @return department name
     */
    public String getDepartment() {
        return department;
    }
    
    /**
     * Sets the department name.
     *
     * @param department department name
     */
    public void setDepartment(String department) {
        this.department = department;
    }
}
