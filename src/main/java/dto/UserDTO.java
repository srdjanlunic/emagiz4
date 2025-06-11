package dto;

import java.util.UUID;

public class UserDTO {
    private UUID id;
    private String username;
    private String email;
    private String roleName;
    // departmentName can be added here later if needed

    public UserDTO(UUID id, String username, String email, String roleName) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.roleName = roleName;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
} 