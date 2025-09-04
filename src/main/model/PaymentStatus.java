package model;

public enum PaymentStatus {
    PENDING("Payment pending"),
    PROCESSING("Payment processing"),
    COMPLETED("Payment completed"),
    FAILED("Payment failed"),
    REFUNDED("Payment refunded"),
    PARTIALLY_REFUNDED("Payment partially refunded"),
    DISPUTED("Payment disputed"),
    CANCELLED("Payment cancelled");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPaid() {
        return this == COMPLETED;
    }

    public boolean isRefundable() {
        return this == COMPLETED;
    }

    public boolean isFinal() {
        return this == COMPLETED || this == FAILED || this == REFUNDED || this == CANCELLED;
    }
}