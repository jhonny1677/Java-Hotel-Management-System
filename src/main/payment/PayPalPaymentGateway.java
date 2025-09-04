package payment;

import model.PaymentResult;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.math.BigDecimal;

public class PayPalPaymentGateway implements PaymentGateway {
    private final String clientId;
    private final String clientSecret;
    private final boolean sandbox;

    public PayPalPaymentGateway(String clientId, String clientSecret, boolean sandbox) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.sandbox = sandbox;
    }

    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        try {
            simulateApiDelay();
            
            if (!isValidPaymentMethod(request.getPaymentMethod())) {
                return new PaymentResult(false, null, "Invalid payment method for PayPal");
            }

            boolean success = simulatePayPalPayment(request);
            
            if (success) {
                String paypalPaymentId = generatePayPalPaymentId();
                return new PaymentResult(true, paypalPaymentId, "Payment processed successfully via PayPal");
            } else {
                return new PaymentResult(false, null, "PayPal payment declined or cancelled");
            }
            
        } catch (Exception e) {
            return new PaymentResult(false, null, "PayPal gateway error: " + e.getMessage());
        }
    }

    @Override
    public PaymentResult processRefund(RefundRequest request) {
        try {
            simulateApiDelay();
            
            boolean success = simulatePayPalRefund(request);
            
            if (success) {
                String refundId = generatePayPalRefundId();
                return new PaymentResult(true, refundId, "Refund processed successfully via PayPal");
            } else {
                return new PaymentResult(false, null, "PayPal refund failed");
            }
            
        } catch (Exception e) {
            return new PaymentResult(false, null, "PayPal refund error: " + e.getMessage());
        }
    }

    @Override
    public PaymentResult verifyPayment(String paymentId) {
        try {
            simulateApiDelay();
            
            if (paymentId != null && (paymentId.startsWith("PAYID") || paymentId.startsWith("PAY-"))) {
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
            simulateApiDelay(100);
            return Math.random() > 0.03; // 97% uptime simulation
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getGatewayName() {
        return "PayPal";
    }

    @Override
    public Map<String, Object> getGatewayInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "PayPal");
        info.put("version", "v2");
        info.put("sandbox", sandbox);
        info.put("supportedMethods", new String[]{
            "PAYPAL", "CREDIT_CARD", "DEBIT_CARD"
        });
        info.put("supportedCurrencies", new String[]{
            "USD", "EUR", "GBP", "CAD", "AUD", "JPY", "CHF", "SEK", "NOK", "DKK"
        });
        info.put("fees", Map.of(
            "domestic", "2.9% + $0.30",
            "international", "4.4% + fixed fee",
            "paypalAccount", "5.0% + fixed fee"
        ));
        return info;
    }

    private boolean isValidPaymentMethod(PaymentMethod paymentMethod) {
        if (paymentMethod == null) return false;
        
        PaymentMethod.PaymentType type = paymentMethod.getType();
        return type == PaymentMethod.PaymentType.PAYPAL ||
               type == PaymentMethod.PaymentType.CREDIT_CARD ||
               type == PaymentMethod.PaymentType.DEBIT_CARD;
    }

    private boolean simulatePayPalPayment(PaymentRequest request) {
        // PayPal has slightly different success rates
        BigDecimal amount = request.getAmount();
        
        if (request.getPaymentMethod().getType() == PaymentMethod.PaymentType.PAYPAL) {
            // Higher success rate for PayPal accounts
            return Math.random() > 0.01; // 99% success rate
        } else {
            // Credit/Debit cards through PayPal
            if (amount.compareTo(BigDecimal.valueOf(5000)) >= 0) {
                return Math.random() > 0.15; // 85% success rate for high amounts
            } else {
                return Math.random() > 0.05; // 95% success rate for normal amounts
            }
        }
    }

    private boolean simulatePayPalRefund(RefundRequest request) {
        return Math.random() > 0.01; // 99% success rate for refunds
    }

    private void simulateApiDelay() {
        simulateApiDelay(300); // PayPal tends to be slightly slower
    }

    private void simulateApiDelay(int maxDelay) {
        try {
            Thread.sleep((long) (Math.random() * maxDelay));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private String generatePayPalPaymentId() {
        return "PAYID-" + UUID.randomUUID().toString().replace("-", "").substring(0, 20).toUpperCase();
    }

    private String generatePayPalRefundId() {
        return "RF-" + UUID.randomUUID().toString().replace("-", "").substring(0, 22).toUpperCase();
    }
}