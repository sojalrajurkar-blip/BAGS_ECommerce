package com.rora.backend.returns.entity;

public enum InspectionStatus {
    PENDING_DELIVERY("Pending Delivery"),
    AWAITING_HUB_DELIVERY("Awaiting Hub Delivery"),
    PASSED_PRISTINE("Passed (Pristine Condition)"),
    PASSED_WITH_CONDITIONS("Passed with Conditions"),
    FAILED_POLICY_CHECK("Failed Policy Check"),
    REJECTED_DAMAGED("Rejected (Damaged / Missing Items)");

    private final String displayName;

    InspectionStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static InspectionStatus fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return PENDING_DELIVERY;
        }
        for (InspectionStatus status : InspectionStatus.values()) {
            if (status.name().equalsIgnoreCase(text.trim()) ||
                status.displayName.equalsIgnoreCase(text.trim()) ||
                status.name().replace("_", " ").equalsIgnoreCase(text.trim())) {
                return status;
            }
        }
        return PENDING_DELIVERY;
    }
}
