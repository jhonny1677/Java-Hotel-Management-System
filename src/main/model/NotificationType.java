package model;

public enum NotificationType {
    BOOKING_CONFIRMATION("Booking Confirmation"),
    BOOKING_CANCELLATION("Booking Cancellation"),
    PAYMENT_CONFIRMATION("Payment Confirmation"),
    PAYMENT_FAILURE("Payment Failure"),
    ROOM_AVAILABILITY("Room Availability"),
    PROMOTIONAL("Promotional Offer"),
    SYSTEM_MAINTENANCE("System Maintenance"),
    WELCOME("Welcome Message");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}