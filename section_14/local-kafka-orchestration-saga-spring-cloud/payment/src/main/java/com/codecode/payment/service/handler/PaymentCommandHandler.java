package com.codecode.payment.service.handler;

import com.codecode.core.dto.command.ProcessPaymentCommand;
import com.codecode.core.dto.event.PaymentFailedEvent;
import com.codecode.core.dto.event.PaymentProcessedEvent;
import com.codecode.payment.entity.Payment;
import com.codecode.payment.service.PaymentService;
import jakarta.ws.rs.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics={"${app.kafka.payment-command-topic}"})
public class PaymentCommandHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentCommandHandler.class);
    private final PaymentService paymentService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.payment-event-topic}")
    private String paymentEventTopic;

    public PaymentCommandHandler(PaymentService paymentService, KafkaTemplate<String, Object> kafkaTemplate) {
        this.paymentService = paymentService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaHandler
    public void handler(ProcessPaymentCommand command) {
        LOGGER.info("Process Payment Command: {}", command.getProcessPaymentCommandToString());
        try {
            //test for payment failed event
            throwException();
            //process the payment and save the payment to the relational database
            Payment payment = new Payment(command.getOrderId(), command.getTotalPrice());
            paymentService.process(payment);
            PaymentProcessedEvent paymentProcessedEvent = new PaymentProcessedEvent(
                    payment.getPaymentId(), payment.getOrderId(), payment.getTotalPrice());
            //send to Kafka broker
            kafkaTemplate.send(paymentEventTopic, paymentProcessedEvent);
            LOGGER.info("Payment Processed Event: {}", paymentProcessedEvent.toString());
        } catch (Exception e) {
            PaymentFailedEvent paymentFailedEvent = new PaymentFailedEvent(
                    command.getOrderId(), command.getTotalPrice(), command.getFoodItemReservationList());
            //send to Kafka broker
            kafkaTemplate.send(paymentEventTopic, paymentFailedEvent);
            LOGGER.info("Payment Failed Event: {}", paymentFailedEvent.toString());
        }
    }

    private void throwException() {
        throw new NotFoundException();
    }
}
