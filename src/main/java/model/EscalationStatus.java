package model;

public enum EscalationStatus {
    NOT_ESCALATED("not_escalated"),
    ESCALATED("escalated"),
    REVIEWED("reviewed");
    
    private final String value;
    
    EscalationStatus(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static EscalationStatus fromValue(String value) {
        for (EscalationStatus status : EscalationStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown escalation status: " + value);
    }
}