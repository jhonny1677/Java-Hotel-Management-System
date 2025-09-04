package payment;

import java.math.BigDecimal;
import java.util.Map;
import java.util.HashMap;

public class RefundRequest {
    private final String refundId;
    private final String originalPaymentId;
    private final BigDecimal amount;
    private final String reason;
    private final Map<String, String> metadata;
    private final boolean isPartialRefund;

    private RefundRequest(Builder builder) {
        this.refundId = builder.refundId;
        this.originalPaymentId = builder.originalPaymentId;
        this.amount = builder.amount;
        this.reason = builder.reason;
        this.metadata = new HashMap<>(builder.metadata);
        this.isPartialRefund = builder.isPartialRefund;
    }

    // Getters
    public String getRefundId() { return refundId; }
    public String getOriginalPaymentId() { return originalPaymentId; }
    public BigDecimal getAmount() { return amount; }
    public String getReason() { return reason; }
    public Map<String, String> getMetadata() { return new HashMap<>(metadata); }
    public boolean isPartialRefund() { return isPartialRefund; }

    public static class Builder {
        private String refundId;
        private String originalPaymentId;
        private BigDecimal amount;
        private String reason;
        private Map<String, String> metadata = new HashMap<>();
        private boolean isPartialRefund;

        public Builder refundId(String refundId) {
            this.refundId = refundId;
            return this;
        }

        public Builder originalPaymentId(String originalPaymentId) {
            this.originalPaymentId = originalPaymentId;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder amount(double amount) {
            this.amount = BigDecimal.valueOf(amount);
            return this;
        }

        public Builder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public Builder metadata(String key, String value) {
            this.metadata.put(key, value);
            return this;
        }

        public Builder partialRefund(boolean isPartialRefund) {
            this.isPartialRefund = isPartialRefund;
            return this;
        }

        public RefundRequest build() {
            if (refundId == null || originalPaymentId == null || amount == null) {
                throw new IllegalArgumentException("RefundId, OriginalPaymentId, and Amount are required");
            }
            return new RefundRequest(this);
        }
    }

    @Override
    public String toString() {
        return String.format("RefundRequest{refundId='%s', originalPaymentId='%s', amount=%s}", 
                           refundId, originalPaymentId, amount);
    }
}