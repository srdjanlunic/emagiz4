package model;

/**
 * Enumeration representing various statuses for a vulnerability assessment.
 */
public enum AssessmentStatus {
    
    /** The functionality related to the vulnerability is not used. */
    FUNCTIONALITY_NOT_USED("functionality_not_used"),
    
    /** A newer version of the affected component is used. */
    NEWER_VERSION("newer_version"),
    
    /** The vulnerability's attack vector is not available in the system. */
    ATTACK_VECTOR_NOT_AVAILABLE("attack_vector_not_available"),
    
    /** The risk has been accepted and no action will be taken. */
    ACCEPTED_RISK("accepted_risk"),
    
    /** The vulnerability has been resolved. */
    RESOLVED("resolved"),
    
    /** An update is planned to address the vulnerability. */
    UPDATE_PLANNED("update_planned"),
    
    /** A system change is planned to mitigate the vulnerability. */
    CHANGE_PLANNED("change_planned"),
    
    /** Status is unknown or not specified. */
    UNKNOWN("unknown");
    
    private final String value;
    
    /**
     * Constructor to associate a string value with the status.
     *
     * @param value the string representation of the status
     */
    AssessmentStatus(String value) {
        this.value = value;
    }
    
    /**
     * Gets the string representation of the status.
     *
     * @return string value of the status
     */
    public String getValue() {
        return value;
    }
    
    /**
     * Returns the corresponding {@code AssessmentStatus} for the given string value.
     *
     * @param value string value to match
     * @return matching {@code AssessmentStatus}
     * @throws IllegalArgumentException if no matching status is found
     */
    public static AssessmentStatus fromValue(String value) {
        for (AssessmentStatus status : AssessmentStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown assessment status: " + value);
    }
}
