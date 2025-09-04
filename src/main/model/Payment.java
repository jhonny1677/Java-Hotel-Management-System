package model;

import java.time.LocalDateTime;
import org.json.JSONObject;
import persistence.Writable;

public class Payment implements Writable {
    private Long id;
    private String paymentId;
    private Long userId;
    private double amount;
    private String description;
    private PaymentStatus status;
    private LocalDateTime paymentDate;
    private String failureReason;
    private String originalPaymentId; // For refunds

    public Payment() {
        this.paymentDate = LocalDateTime.now();
        this.status = PaymentStatus.PENDING;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }

    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }

    public String getOriginalPaymentId() { return originalPaymentId; }
    public void setOriginalPaymentId(String originalPaymentId) { this.originalPaymentId = originalPaymentId; }

    public boolean isRefund() {
        return originalPaymentId != null;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("paymentId", paymentId);
        json.put("userId", userId);
        json.put("amount", amount);
        json.put("description", description);
        json.put("status", status != null ? status.name() : null);
        json.put("paymentDate", paymentDate != null ? paymentDate.toString() : null);
        json.put("failureReason", failureReason);
        json.put("originalPaymentId", originalPaymentId);
        return json;
    }

    @Override
    public String toString() {
        return String.format("Payment{id=%d, paymentId='%s', amount=%.2f, status=%s}", 
                           id, paymentId, amount, status);
    }
}