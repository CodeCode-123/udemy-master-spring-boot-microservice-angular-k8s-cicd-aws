package com.codecode.payment.service;

import com.codecode.payment.entity.Payment;


import java.util.List;

public interface PaymentService {
    Payment process(Payment payment);
    List<Payment> getAllPayments();
    Payment getPaymentById(int paymentId);
    Payment getPaymentByOrderId(int orderId);
}
