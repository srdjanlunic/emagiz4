package model;

public enum AssessmentStatus {
    FUNCTIONALITY_NOT_USED("functionality_not_used"),
    NEWER_VERSION("newer_version"),
    ATTACK_VECTOR_NOT_AVAILABLE("attack_vector_not_available"),
    ACCEPTED_RISK("accepted_risk"),
    UPDATE_PLANNED("update_planned"),
    CHANGE_PLANNED("change_planned"),
    UNKNOWN("unknown");

    private final String value;

    AssessmentStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static AssessmentStatus fromValue(String value) {
        for (AssessmentStatus status : AssessmentStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown assessment status: " + value);
    }
}
