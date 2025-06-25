package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Represents a user in the system with authentication and profile details.
 */
public class User {
    private UUID id;
    private String username;
    
    @JsonIgnore
    private String password;
    
    private String email;
    private String firstName;
    private String lastName;
    private UUID roleId;
    private UUID organizationId;
    private boolean isActive;
    private Timestamp createdAt;
    private String roleName;
    private Timestamp updatedAt;
    private String rawNvdData;
    
    /**
     * Default constructor initializes creation timestamp and active flag.
     */
    public User() {
        this.isActive = true;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }
    
    /**
     * Constructs a user with specified details.
     * @param username user's login username
     * @param password user's password (hashed)
     * @param email user's email address
     * @param firstName user's first name
     * @param lastName user's last name
     * @param roleId UUID of user's role
     * @param organizationId UUID of user's organization
     */
    public User(String username, String password, String email, String firstName,
                String lastName, UUID roleId, UUID organizationId) {
        this();
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roleId = roleId;
        this.organizationId = organizationId;
    }
    
    /** @return user ID */
    public UUID getId() { return id; }
    
    /** @param id user ID to set */
    public void setId(UUID id) { this.id = id; }
    
    /** @return username */
    public String getUsername() { return username; }
    
    /** @param username username to set */
    public void setUsername(String username) { this.username = username; }
    
    /** @return hashed password (ignored in JSON serialization) */
    @JsonIgnore
    public String getPassword() { return password; }
    
    /** @param password hashed password to set (used in JSON deserialization) */
    @JsonProperty
    public void setPassword(String password) { this.password = password; }
    
    /** @return email */
    public String getEmail() { return email; }
    
    /** @param email email to set */
    public void setEmail(String email) { this.email = email; }
    
    /** @return first name */
    public String getFirstName() { return firstName; }
    
    /** @param firstName first name to set */
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    /** @return last name */
    public String getLastName() { return lastName; }
    
    /** @param lastName last name to set */
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    /** @return role ID */
    public UUID getRoleId() { return roleId; }
    
    /** @param roleId role ID to set */
    public void setRoleId(UUID roleId) { this.roleId = roleId; }
    
    /** @return organization ID */
    public UUID getOrganizationId() { return organizationId; }
    
    /** @param organizationId organization ID to set */
    public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }
    
    /** @return true if user is active */
    public boolean isActive() { return isActive; }
    
    /** @param active set whether user is active */
    public void setActive(boolean active) { isActive = active; }
    
    /** @return timestamp when user was created */
    public Timestamp getCreatedAt() { return createdAt; }
    
    /** @param createdAt creation timestamp to set */
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    /** @return role name */
    public String getRoleName() { return roleName; }
    
    /** @param roleName role name to set */
    public void setRoleName(String roleName) { this.roleName = roleName; }
    
    /** @return timestamp when user was last updated */
    public Timestamp getUpdatedAt() { return updatedAt; }
    
    /** @param updatedAt update timestamp to set */
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    
    /** @return raw NVD data as JSON string */
    public String getRawNvdData() { return rawNvdData; }
    
    /** @param rawNvdData raw NVD JSON data to set */
    public void setRawNvdData(String rawNvdData) { this.rawNvdData = rawNvdData; }
    
    /**
     * Returns the full name by concatenating first and last names.
     * @return full name string
     */
    @JsonIgnore
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", roleId=" + roleId +
                ", roleName='" + roleName + '\'' +
                ", organizationId=" + organizationId +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        User user = (User) o;
        return id != null && id.equals(user.id);
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
