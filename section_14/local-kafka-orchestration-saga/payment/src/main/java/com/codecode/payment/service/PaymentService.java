package com.codecode.payment.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentService.class);

//    @KafkaListener(topics = "fetch-orderpaymentdto")
//    public String getOrderPaymentDTO(String s) {
//        LOGGER.info("Fetched order payment dto: {}", s);
//        return s;
//    }
}
