package model;

/**
 * Enum representing different user roles in the system.
 */
public enum UserRole {
    ADMIN("admin"),
    SECURITY_OFFICER("security_officer"),
    SYSTEM_OWNER("system_owner"),
    TECHNICAL_EXPERT("technical_expert");
    
    private final String value;
    
    /**
     * Constructs a UserRole with the given string value.
     * @param value string representation of the role
     */
    UserRole(String value) {
        this.value = value;
    }
    
    /**
     * Gets the string value of the user role.
     * @return the string representation of the role
     */
    public String getValue() {
        return value;
    }
    
    /**
     * Parses a string value and returns the corresponding UserRole enum.
     * @param value string representation of the role
     * @return UserRole matching the given string
     * @throws IllegalArgumentException if no matching role is found
     */
    public static UserRole fromValue(String value) {
        for (UserRole role : UserRole.values()) {
            if (role.value.equals(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + value);
    }
}
