package com.codecode.payment.repository;

import com.codecode.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, Integer> {
    Optional<Payment> findByOrderId(Integer orderId);
}
