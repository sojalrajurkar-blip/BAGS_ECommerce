package com.rora.backend.review.entity;

public enum ReviewStatus {
    PUBLISHED("Published"),
    PENDING_MODERATION("Pending Moderation"),
    FLAGGED("Flagged"),
    ARCHIVED("Archived"),
    REJECTED("Rejected");

    private final String displayName;

    ReviewStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ReviewStatus fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return PUBLISHED;
        }
        for (ReviewStatus status : ReviewStatus.values()) {
            if (status.name().equalsIgnoreCase(text.trim()) ||
                status.displayName.equalsIgnoreCase(text.trim()) ||
                status.name().replace("_", " ").equalsIgnoreCase(text.trim())) {
                return status;
            }
        }
        return PUBLISHED;
    }
}
