package com.codecode.payment.service.impl;

import com.codecode.payment.entity.Payment;
import com.codecode.payment.repository.PaymentRepo;
import com.codecode.payment.service.PaymentService;
import jakarta.ws.rs.NotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepo paymentRepo;

    public PaymentServiceImpl(PaymentRepo paymentRepo) {
        this.paymentRepo = paymentRepo;
    }

    @Override
    public Payment process(Payment payment) {
        return paymentRepo.save(payment);
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepo.findAll();
    }

    @Override
    public Payment getPaymentById(int paymentId) {
        Optional<Payment> paymentOptional = paymentRepo.findByOrderId(paymentId);
        if (paymentOptional.isEmpty()) {
            throw new NotFoundException("Payment is not found by paymentId: " + paymentId);
        }
        return paymentOptional.get();
    }

    @Override
    public Payment getPaymentByOrderId(int orderId) {
        Optional<Payment> paymentOptional = paymentRepo.findByOrderId(orderId);
        if (paymentOptional.isEmpty()) {
            throw new NotFoundException("Payment is not found by orderId: " + orderId);
        }
        return paymentOptional.get();
    }
}
