package com.rora.backend.returns.entity;

public enum ReturnStatus {
    REQUESTED("Requested"),
    UNDER_REVIEW("Under Review"),
    APPROVED("Approved"),
    PICKUP_SCHEDULED("Pickup Scheduled"),
    RECEIVED_AT_HUB("Received at Hub"),
    INSPECTED("Inspected"),
    APPROVED_AND_REFUNDED("Approved & Refunded"),
    REJECTED("Rejected"),
    CANCELLED("Cancelled");

    private final String displayName;

    ReturnStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ReturnStatus fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return REQUESTED;
        }
        for (ReturnStatus status : ReturnStatus.values()) {
            if (status.name().equalsIgnoreCase(text.trim()) ||
                status.displayName.equalsIgnoreCase(text.trim()) ||
                status.name().replace("_", " ").equalsIgnoreCase(text.trim())) {
                return status;
            }
        }
        return REQUESTED;
    }
}
