package com.rora.backend.shipment.entity;

public enum ShipmentStatus {
    CREATED("Created"),
    MANIFESTED("Manifested"),
    PICKED_UP("Picked Up"),
    IN_TRANSIT("In Transit"),
    OUT_FOR_DELIVERY("Out for Delivery"),
    DELIVERED("Delivered"),
    FAILED_DELIVERY("Failed Delivery"),
    RETURNED_TO_ORIGIN("Returned to Origin"),
    CANCELLED("Cancelled");

    private final String displayName;

    ShipmentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ShipmentStatus fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return CREATED;
        }
        for (ShipmentStatus status : ShipmentStatus.values()) {
            if (status.name().equalsIgnoreCase(text.trim()) ||
                status.displayName.equalsIgnoreCase(text.trim()) ||
                status.name().replace("_", " ").equalsIgnoreCase(text.trim())) {
                return status;
            }
        }
        return CREATED;
    }
}
