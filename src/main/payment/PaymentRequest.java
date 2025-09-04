package payment;

import java.math.BigDecimal;
import java.util.Map;
import java.util.HashMap;

public class PaymentRequest {
    private final String paymentId;
    private final Long userId;
    private final BigDecimal amount;
    private final String currency;
    private final String description;
    private final PaymentMethod paymentMethod;
    private final Map<String, String> metadata;
    private final String callbackUrl;
    private final boolean captureImmediately;

    private PaymentRequest(Builder builder) {
        this.paymentId = builder.paymentId;
        this.userId = builder.userId;
        this.amount = builder.amount;
        this.currency = builder.currency;
        this.description = builder.description;
        this.paymentMethod = builder.paymentMethod;
        this.metadata = new HashMap<>(builder.metadata);
        this.callbackUrl = builder.callbackUrl;
        this.captureImmediately = builder.captureImmediately;
    }

    // Getters
    public String getPaymentId() { return paymentId; }
    public Long getUserId() { return userId; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getDescription() { return description; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public Map<String, String> getMetadata() { return new HashMap<>(metadata); }
    public String getCallbackUrl() { return callbackUrl; }
    public boolean isCaptureImmediately() { return captureImmediately; }

    public static class Builder {
        private String paymentId;
        private Long userId;
        private BigDecimal amount;
        private String currency = "USD";
        private String description;
        private PaymentMethod paymentMethod;
        private Map<String, String> metadata = new HashMap<>();
        private String callbackUrl;
        private boolean captureImmediately = true;

        public Builder paymentId(String paymentId) {
            this.paymentId = paymentId;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
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

        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder paymentMethod(PaymentMethod paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public Builder metadata(String key, String value) {
            this.metadata.put(key, value);
            return this;
        }

        public Builder metadata(Map<String, String> metadata) {
            this.metadata.putAll(metadata);
            return this;
        }

        public Builder callbackUrl(String callbackUrl) {
            this.callbackUrl = callbackUrl;
            return this;
        }

        public Builder captureImmediately(boolean captureImmediately) {
            this.captureImmediately = captureImmediately;
            return this;
        }

        public PaymentRequest build() {
            if (paymentId == null || userId == null || amount == null) {
                throw new IllegalArgumentException("PaymentId, UserId, and Amount are required");
            }
            return new PaymentRequest(this);
        }
    }

    @Override
    public String toString() {
        return String.format("PaymentRequest{paymentId='%s', userId=%d, amount=%s, currency='%s'}", 
                           paymentId, userId, amount, currency);
    }
}