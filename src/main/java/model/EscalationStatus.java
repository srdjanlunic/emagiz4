package model;

/**
 * Represents the escalation status of a vulnerability or issue.
 */
public enum EscalationStatus {
    /** Indicates that the issue has not been escalated. */
    NOT_ESCALATED("not_escalated"),
    
    /** Indicates that the issue has been escalated. */
    ESCALATED("escalated"),
    
    /** Indicates that the issue has been reviewed after escalation. */
    REVIEWED("reviewed");
    
    private final String value;
    
    /**
     * Constructs an EscalationStatus enum with the specified string value.
     *
     * @param value the string representation of the escalation status
     */
    EscalationStatus(String value) {
        this.value = value;
    }
    
    /**
     * Returns the string representation of the escalation status.
     *
     * @return the string value of the status
     */
    public String getValue() {
        return value;
    }
    
    /**
     * Converts a string value to the corresponding EscalationStatus enum.
     *
     * @param value the string value to convert
     * @return the corresponding EscalationStatus enum
     * @throws IllegalArgumentException if the value does not match any status
     */
    public static EscalationStatus fromValue(String value) {
        for (EscalationStatus status : EscalationStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown escalation status: " + value);
    }
}
