package payment;

import model.PaymentResult;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

public class PaymentGatewayManager {
    private final Map<String, PaymentGateway> gateways;
    private final Map<String, AtomicInteger> failureCount;
    private final Map<String, Long> lastHealthCheck;
    private final PaymentGatewayRouter router;
    private final int maxFailures;
    private final long healthCheckInterval;

    public PaymentGatewayManager() {
        this(3, 60000); // 3 failures, 1 minute health check interval
    }

    public PaymentGatewayManager(int maxFailures, long healthCheckInterval) {
        this.gateways = new ConcurrentHashMap<>();
        this.failureCount = new ConcurrentHashMap<>();
        this.lastHealthCheck = new ConcurrentHashMap<>();
        this.router = new PaymentGatewayRouter();
        this.maxFailures = maxFailures;
        this.healthCheckInterval = healthCheckInterval;
    }

    public void registerGateway(String name, PaymentGateway gateway) {
        gateways.put(name, gateway);
        failureCount.put(name, new AtomicInteger(0));
        lastHealthCheck.put(name, 0L);
        router.addGateway(name, gateway);
    }

    public PaymentResult processPayment(PaymentRequest request) {
        List<String> availableGateways = getHealthyGateways();
        if (availableGateways.isEmpty()) {
            return new PaymentResult(false, null, "No healthy payment gateways available");
        }

        // Route payment to best gateway
        String selectedGateway = router.selectGateway(request, availableGateways);
        PaymentGateway gateway = gateways.get(selectedGateway);

        try {
            PaymentResult result = gateway.processPayment(request);
            
            if (result.isSuccessful()) {
                // Reset failure count on success
                failureCount.get(selectedGateway).set(0);
            } else {
                // Increment failure count
                int failures = failureCount.get(selectedGateway).incrementAndGet();
                if (failures >= maxFailures) {
                    // Try backup gateway if primary fails
                    return tryBackupGateway(request, selectedGateway, availableGateways);
                }
            }
            
            return result;
            
        } catch (Exception e) {
            failureCount.get(selectedGateway).incrementAndGet();
            return tryBackupGateway(request, selectedGateway, availableGateways);
        }
    }

    private PaymentResult tryBackupGateway(PaymentRequest request, String excludeGateway, 
                                         List<String> availableGateways) {
        List<String> backupGateways = new ArrayList<>(availableGateways);
        backupGateways.remove(excludeGateway);
        
        if (backupGateways.isEmpty()) {
            return new PaymentResult(false, null, "All payment gateways failed");
        }

        String backupGateway = router.selectGateway(request, backupGateways);
        PaymentGateway gateway = gateways.get(backupGateway);
        
        try {
            return gateway.processPayment(request);
        } catch (Exception e) {
            return new PaymentResult(false, null, "Backup payment gateway failed: " + e.getMessage());
        }
    }

    public PaymentResult processRefund(RefundRequest request) {
        // For refunds, try to use the same gateway that processed the original payment
        String preferredGateway = extractGatewayFromPaymentId(request.getOriginalPaymentId());
        
        if (preferredGateway != null && isGatewayHealthy(preferredGateway)) {
            try {
                return gateways.get(preferredGateway).processRefund(request);
            } catch (Exception e) {
                // Fall through to try other gateways
            }
        }

        // If preferred gateway fails, try others
        List<String> availableGateways = getHealthyGateways();
        for (String gatewayName : availableGateways) {
            try {
                PaymentResult result = gateways.get(gatewayName).processRefund(request);
                if (result.isSuccessful()) {
                    return result;
                }
            } catch (Exception e) {
                // Continue to next gateway
            }
        }

        return new PaymentResult(false, null, "All refund attempts failed");
    }

    public List<String> getHealthyGateways() {
        List<String> healthy = new ArrayList<>();
        long now = System.currentTimeMillis();

        for (String gatewayName : gateways.keySet()) {
            if (isGatewayHealthy(gatewayName)) {
                // Check if health check is needed
                long lastCheck = lastHealthCheck.get(gatewayName);
                if (now - lastCheck > healthCheckInterval) {
                    boolean isHealthy = gateways.get(gatewayName).isHealthy();
                    lastHealthCheck.put(gatewayName, now);
                    
                    if (isHealthy) {
                        failureCount.get(gatewayName).set(0);
                        healthy.add(gatewayName);
                    }
                } else {
                    healthy.add(gatewayName);
                }
            }
        }

        return healthy;
    }

    private boolean isGatewayHealthy(String gatewayName) {
        return failureCount.get(gatewayName).get() < maxFailures;
    }

    private String extractGatewayFromPaymentId(String paymentId) {
        if (paymentId == null) return null;
        
        if (paymentId.startsWith("pi_")) {
            return "Stripe";
        } else if (paymentId.startsWith("PAYID") || paymentId.startsWith("PAY-")) {
            return "PayPal";
        }
        
        return null;
    }

    public Map<String, Object> getGatewayStatuses() {
        Map<String, Object> statuses = new HashMap<>();
        
        for (String gatewayName : gateways.keySet()) {
            Map<String, Object> status = new HashMap<>();
            status.put("healthy", isGatewayHealthy(gatewayName));
            status.put("failures", failureCount.get(gatewayName).get());
            status.put("lastHealthCheck", new Date(lastHealthCheck.get(gatewayName)));
            status.put("info", gateways.get(gatewayName).getGatewayInfo());
            
            statuses.put(gatewayName, status);
        }
        
        return statuses;
    }

    public void resetGatewayFailures(String gatewayName) {
        if (failureCount.containsKey(gatewayName)) {
            failureCount.get(gatewayName).set(0);
        }
    }

    public void forceHealthCheck() {
        for (String gatewayName : gateways.keySet()) {
            lastHealthCheck.put(gatewayName, 0L);
        }
    }

    private static class PaymentGatewayRouter {
        private final Map<String, PaymentGateway> gatewayMap = new HashMap<>();

        public void addGateway(String name, PaymentGateway gateway) {
            gatewayMap.put(name, gateway);
        }

        public String selectGateway(PaymentRequest request, List<String> availableGateways) {
            if (availableGateways.isEmpty()) {
                throw new IllegalArgumentException("No available gateways");
            }

            // Simple routing logic based on payment method and amount
            PaymentMethod.PaymentType paymentType = request.getPaymentMethod().getType();
            BigDecimal amount = request.getAmount();

            // Prefer Stripe for card payments
            if ((paymentType == PaymentMethod.PaymentType.CREDIT_CARD || 
                 paymentType == PaymentMethod.PaymentType.DEBIT_CARD ||
                 paymentType == PaymentMethod.PaymentType.APPLE_PAY ||
                 paymentType == PaymentMethod.PaymentType.GOOGLE_PAY) && 
                availableGateways.contains("Stripe")) {
                return "Stripe";
            }

            // Prefer PayPal for PayPal payments or high amounts
            if ((paymentType == PaymentMethod.PaymentType.PAYPAL ||
                 amount.compareTo(BigDecimal.valueOf(1000)) > 0) &&
                availableGateways.contains("PayPal")) {
                return "PayPal";
            }

            // Default to first available gateway
            return availableGateways.get(0);
        }
    }
}