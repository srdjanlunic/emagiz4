package model;

public enum UserRole {
    ADMIN("admin"),
    SECURITY_OFFICER("security_officer"),
    SYSTEM_OWNER("system_owner"),
    TECHNICAL_EXPERT("technical_expert");

    private final String value;

    UserRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static UserRole fromValue(String value) {
        for (UserRole role : UserRole.values()) {
            if (role.value.equals(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + value);
    }
}
