package model;

public enum BookingStatus {
    PENDING("Pending confirmation"),
    CONFIRMED("Confirmed"),
    CHECKED_IN("Checked in"),
    CHECKED_OUT("Checked out"),
    CANCELLED("Cancelled"),
    NO_SHOW("No show");

    private final String description;

    BookingStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCancellable() {
        return this == PENDING || this == CONFIRMED;
    }

    public boolean isActive() {
        return this != CANCELLED && this != NO_SHOW && this != CHECKED_OUT;
    }
}