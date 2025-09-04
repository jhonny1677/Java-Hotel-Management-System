package payment;

import model.PaymentResult;
import java.util.Map;

public interface PaymentGateway {
    PaymentResult processPayment(PaymentRequest request);
    PaymentResult processRefund(RefundRequest request);
    PaymentResult verifyPayment(String paymentId);
    boolean isHealthy();
    String getGatewayName();
    Map<String, Object> getGatewayInfo();
}