package repository.impl;

import model.Payment;
import model.PaymentStatus;
import repository.PaymentRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class InMemoryPaymentRepository implements PaymentRepository {
    private final Map<Long, Payment> payments = new ConcurrentHashMap<>();
    private final Map<String, Payment> paymentsByPaymentId = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Payment save(Payment payment) {
        if (payment.getId() == null) {
            payment.setId(idGenerator.getAndIncrement());
        }
        payments.put(payment.getId(), payment);
        if (payment.getPaymentId() != null) {
            paymentsByPaymentId.put(payment.getPaymentId(), payment);
        }
        return payment;
    }

    @Override
    public Optional<Payment> findById(Long id) {
        return Optional.ofNullable(payments.get(id));
    }

    @Override
    public List<Payment> findAll() {
        return new ArrayList<>(payments.values());
    }

    @Override
    public boolean deleteById(Long id) {
        Payment payment = payments.remove(id);
        if (payment != null && payment.getPaymentId() != null) {
            paymentsByPaymentId.remove(payment.getPaymentId());
            return true;
        }
        return false;
    }

    @Override
    public void delete(Payment payment) {
        if (payment.getId() != null) {
            deleteById(payment.getId());
        }
    }

    @Override
    public boolean exists(Long id) {
        return payments.containsKey(id);
    }

    @Override
    public long count() {
        return payments.size();
    }

    @Override
    public Optional<Payment> findByPaymentId(String paymentId) {
        return Optional.ofNullable(paymentsByPaymentId.get(paymentId));
    }

    @Override
    public List<Payment> findByUserId(Long userId) {
        return payments.values().stream()
                .filter(payment -> payment.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findSuccessfulPayments() {
        return payments.values().stream()
                .filter(payment -> payment.getStatus() == PaymentStatus.COMPLETED)
                .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findFailedPayments() {
        return payments.values().stream()
                .filter(payment -> payment.getStatus() == PaymentStatus.FAILED)
                .collect(Collectors.toList());
    }
}