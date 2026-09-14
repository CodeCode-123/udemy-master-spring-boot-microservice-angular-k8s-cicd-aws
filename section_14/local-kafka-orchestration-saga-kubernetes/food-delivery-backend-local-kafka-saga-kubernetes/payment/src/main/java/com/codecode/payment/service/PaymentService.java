package com.codecode.payment.service;

import com.codecode.payment.entity.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

public interface PaymentService {
    Payment process(Payment payment);
    List<Payment> getAllPayments();
    Payment getPaymentById(int paymentId);
    Payment getPaymentByOrderId(int orderId);
}
