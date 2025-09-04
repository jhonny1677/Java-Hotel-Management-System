package services;

import model.Payment;
import model.PaymentResult;
import model.PaymentStatus;
import repository.PaymentRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

public class PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public PaymentResult processPayment(Long userId, double amount, String description) {
        try {
            // Simulate payment processing (in real world, integrate with Stripe/PayPal)
            String paymentId = generatePaymentId();
            
            Payment payment = new Payment();
            payment.setPaymentId(paymentId);
            payment.setUserId(userId);
            payment.setAmount(amount);
            payment.setDescription(description);
            payment.setPaymentDate(LocalDateTime.now());
            payment.setStatus(PaymentStatus.PROCESSING);
            
            // Save payment record
            paymentRepository.save(payment);
            
            // Simulate payment gateway call
            boolean paymentSuccessful = simulatePaymentGateway(amount);
            
            if (paymentSuccessful) {
                payment.setStatus(PaymentStatus.COMPLETED);
                paymentRepository.save(payment);
                return new PaymentResult(true, paymentId, "Payment processed successfully");
            } else {
                payment.setStatus(PaymentStatus.FAILED);
                payment.setFailureReason("Payment gateway declined");
                paymentRepository.save(payment);
                return new PaymentResult(false, null, "Payment failed");
            }
        } catch (Exception e) {
            return new PaymentResult(false, null, "Payment processing error: " + e.getMessage());
        }
    }

    public PaymentResult processRefund(String originalPaymentId, double amount) {
        try {
            Optional<Payment> originalPaymentOpt = paymentRepository.findByPaymentId(originalPaymentId);
            if (originalPaymentOpt.isEmpty()) {
                return new PaymentResult(false, null, "Original payment not found");
            }

            Payment originalPayment = originalPaymentOpt.get();
            if (originalPayment.getStatus() != PaymentStatus.COMPLETED) {
                return new PaymentResult(false, null, "Original payment not completed");
            }

            String refundId = generatePaymentId();
            Payment refund = new Payment();
            refund.setPaymentId(refundId);
            refund.setUserId(originalPayment.getUserId());
            refund.setAmount(-amount); // Negative amount for refund
            refund.setDescription("Refund for: " + originalPayment.getDescription());
            refund.setPaymentDate(LocalDateTime.now());
            refund.setStatus(PaymentStatus.PROCESSING);
            refund.setOriginalPaymentId(originalPaymentId);

            paymentRepository.save(refund);

            // Simulate refund processing
            boolean refundSuccessful = simulateRefundGateway(amount);

            if (refundSuccessful) {
                refund.setStatus(PaymentStatus.COMPLETED);
                paymentRepository.save(refund);
                return new PaymentResult(true, refundId, "Refund processed successfully");
            } else {
                refund.setStatus(PaymentStatus.FAILED);
                refund.setFailureReason("Refund gateway declined");
                paymentRepository.save(refund);
                return new PaymentResult(false, null, "Refund failed");
            }
        } catch (Exception e) {
            return new PaymentResult(false, null, "Refund processing error: " + e.getMessage());
        }
    }

    public Optional<Payment> getPaymentById(String paymentId) {
        return paymentRepository.findByPaymentId(paymentId);
    }

    public List<Payment> getUserPayments(Long userId) {
        return paymentRepository.findByUserId(userId);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public double getTotalRevenue() {
        return paymentRepository.findAll().stream()
                .filter(p -> p.getStatus() == PaymentStatus.COMPLETED && p.getAmount() > 0)
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    public double getTotalRefunds() {
        return Math.abs(paymentRepository.findAll().stream()
                .filter(p -> p.getStatus() == PaymentStatus.COMPLETED && p.getAmount() < 0)
                .mapToDouble(Payment::getAmount)
                .sum());
    }

    private String generatePaymentId() {
        return "PAY_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    private boolean simulatePaymentGateway(double amount) {
        // Simulate payment success/failure (90% success rate)
        // In real implementation, this would call Stripe/PayPal APIs
        try {
            Thread.sleep(100); // Simulate network call
            return Math.random() > 0.1; // 90% success rate
        } catch (InterruptedException e) {
            return false;
        }
    }

    private boolean simulateRefundGateway(double amount) {
        // Simulate refund success/failure (95% success rate)
        try {
            Thread.sleep(50); // Simulate network call
            return Math.random() > 0.05; // 95% success rate
        } catch (InterruptedException e) {
            return false;
        }
    }
}