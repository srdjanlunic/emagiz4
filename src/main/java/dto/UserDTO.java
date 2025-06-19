package dto;

import java.util.UUID;

public class UserDto {
    private UUID id;
    private String username;
    private String email;
    private String roleName;
    private String departmentName;

    public UserDto(UUID id, String username, String email, String roleName, String departmentName) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.roleName = roleName;
        this.departmentName = departmentName;
    }

    // Getters and setters
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

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
} 