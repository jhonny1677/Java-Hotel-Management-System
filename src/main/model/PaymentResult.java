package model;

public class PaymentResult {
    private final boolean successful;
    private final String paymentId;
    private final String message;

    public PaymentResult(boolean successful, String paymentId, String message) {
        this.successful = successful;
        this.paymentId = paymentId;
        this.message = message;
    }

    public boolean isSuccessful() { return successful; }
    public String getPaymentId() { return paymentId; }
    public String getMessage() { return message; }

    @Override
    public String toString() {
        return String.format("PaymentResult{successful=%s, paymentId='%s', message='%s'}", 
                           successful, paymentId, message);
    }
}