package repository;

import model.Payment;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends Repository<Payment, Long> {
    Optional<Payment> findByPaymentId(String paymentId);
    List<Payment> findByUserId(Long userId);
    List<Payment> findSuccessfulPayments();
    List<Payment> findFailedPayments();
}