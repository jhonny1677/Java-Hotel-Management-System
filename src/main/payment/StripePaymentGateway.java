package payment;

import model.PaymentResult;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.math.BigDecimal;

public class StripePaymentGateway implements PaymentGateway {
    private final String apiKey;
    private final String webhookSecret;
    private final boolean testMode;

    public StripePaymentGateway(String apiKey, String webhookSecret, boolean testMode) {
        this.apiKey = apiKey;
        this.webhookSecret = webhookSecret;
        this.testMode = testMode;
    }

    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        try {
            // Simulate Stripe API call
            simulateApiDelay();
            
            // Validate payment method
            if (!isValidPaymentMethod(request.getPaymentMethod())) {
                return new PaymentResult(false, null, "Invalid payment method for Stripe");
            }

            // Simulate payment processing
            boolean success = simulateStripePayment(request);
            
            if (success) {
                String stripePaymentId = generateStripePaymentId();
                return new PaymentResult(true, stripePaymentId, "Payment processed successfully via Stripe");
            } else {
                return new PaymentResult(false, null, "Stripe payment declined");
            }
            
        } catch (Exception e) {
            return new PaymentResult(false, null, "Stripe gateway error: " + e.getMessage());
        }
    }

    @Override
    public PaymentResult processRefund(RefundRequest request) {
        try {
            simulateApiDelay();
            
            // Simulate refund processing
            boolean success = simulateStripeRefund(request);
            
            if (success) {
                String refundId = generateStripeRefundId();
                return new PaymentResult(true, refundId, "Refund processed successfully via Stripe");
            } else {
                return new PaymentResult(false, null, "Stripe refund failed");
            }
            
        } catch (Exception e) {
            return new PaymentResult(false, null, "Stripe refund error: " + e.getMessage());
        }
    }

    @Override
    public PaymentResult verifyPayment(String paymentId) {
        try {
            simulateApiDelay();
            
            // Simulate payment verification
            if (paymentId != null && paymentId.startsWith("pi_")) {
                return new PaymentResult(true, paymentId, "Payment verified successfully");
            } else {
                return new PaymentResult(false, null, "Payment not found or invalid");
            }
            
        } catch (Exception e) {
            return new PaymentResult(false, null, "Verification error: " + e.getMessage());
        }
    }

    @Override
    public boolean isHealthy() {
        try {
            // Simulate health check API call
            simulateApiDelay(50); // Shorter delay for health check
            return Math.random() > 0.05; // 95% uptime simulation
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getGatewayName() {
        return "Stripe";
    }

    @Override
    public Map<String, Object> getGatewayInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "Stripe");
        info.put("version", "2023-10-16");
        info.put("testMode", testMode);
        info.put("supportedMethods", new String[]{
            "CREDIT_CARD", "DEBIT_CARD", "APPLE_PAY", "GOOGLE_PAY"
        });
        info.put("supportedCurrencies", new String[]{
            "USD", "EUR", "GBP", "CAD", "AUD", "JPY"
        });
        info.put("fees", Map.of(
            "creditCard", "2.9% + $0.30",
            "debitCard", "2.9% + $0.30",
            "applePay", "2.9% + $0.30",
            "googlePay", "2.9% + $0.30"
        ));
        return info;
    }

    private boolean isValidPaymentMethod(PaymentMethod paymentMethod) {
        if (paymentMethod == null) return false;
        
        PaymentMethod.PaymentType type = paymentMethod.getType();
        return type == PaymentMethod.PaymentType.CREDIT_CARD ||
               type == PaymentMethod.PaymentType.DEBIT_CARD ||
               type == PaymentMethod.PaymentType.APPLE_PAY ||
               type == PaymentMethod.PaymentType.GOOGLE_PAY;
    }

    private boolean simulateStripePayment(PaymentRequest request) {
        // Simulate various payment scenarios
        BigDecimal amount = request.getAmount();
        
        // Test amounts for different behaviors
        if (amount.compareTo(BigDecimal.valueOf(4000)) >= 0) {
            return Math.random() > 0.1; // 90% success rate for high amounts
        } else if (amount.compareTo(BigDecimal.valueOf(1000)) >= 0) {
            return Math.random() > 0.05; // 95% success rate for medium amounts
        } else {
            return Math.random() > 0.02; // 98% success rate for low amounts
        }
    }

    private boolean simulateStripeRefund(RefundRequest request) {
        // Simulate refund success rate (typically higher than payment success)
        return Math.random() > 0.02; // 98% success rate
    }

    private void simulateApiDelay() {
        simulateApiDelay(200); // Default 200ms delay
    }

    private void simulateApiDelay(int maxDelay) {
        try {
            Thread.sleep((long) (Math.random() * maxDelay));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private String generateStripePaymentId() {
        return "pi_" + UUID.randomUUID().toString().replace("-", "").substring(0, 24);
    }

    private String generateStripeRefundId() {
        return "re_" + UUID.randomUUID().toString().replace("-", "").substring(0, 24);
    }
}