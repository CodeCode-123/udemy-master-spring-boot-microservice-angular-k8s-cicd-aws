package com.codecode.payment.service.handler;

import com.codecode.payment.dto.command.ProcessPaymentCommand;
import com.codecode.payment.dto.event.PaymentFailedEvent;
import com.codecode.payment.dto.event.PaymentProcessedEvent;
import com.codecode.payment.entity.Payment;
import com.codecode.payment.service.PaymentService;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

@Component
@KafkaListener(topics={"${app.kafka.payment-command-topic}"})
public class PaymentCommandHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentCommandHandler.class);
    private final PaymentService paymentService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.payment-event-topic}")
    private String paymentEventTopic;

    public PaymentCommandHandler(PaymentService paymentService, KafkaTemplate<String, Object> kafkaTemplate, ObjectMapper objectMapper) {
        this.paymentService = paymentService;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @KafkaHandler
    public void handleProcessPaymentCommand(@Payload String commandStr, @Header(name="payment-command", required = false) String paymentCommand/*ProcessPaymentCommand command*/) {
        LOGGER.info("Payment Command: {}", commandStr);
        if ("process-payment-command".equals(paymentCommand)) {
            ProcessPaymentCommand command = objectMapper.readValue(commandStr, ProcessPaymentCommand.class);
            try {
                //process the payment and save the payment to the relational database
                Payment payment = new Payment(command.getOrderId(), command.getTotalPrice());
                paymentService.process(payment);
                PaymentProcessedEvent paymentProcessedEvent = new PaymentProcessedEvent(
                        payment.getPaymentId(), payment.getOrderId(), payment.getTotalPrice());
                String eventStr = objectMapper.writeValueAsString(paymentProcessedEvent);
                //send through producerRecord with headers()
                ProducerRecord<String, Object> record = new ProducerRecord<>(paymentEventTopic, eventStr);
                record.headers().add(new RecordHeader("payment-event", "payment-processed-event".getBytes(StandardCharsets.UTF_8)));
                kafkaTemplate.send(record);
                //send to Kafka broker
//                kafkaTemplate.send(paymentEventTopic, eventStr);
                LOGGER.info("Payment Processed Event: {}", paymentProcessedEvent.toString());
            } catch (Exception e) {
                PaymentFailedEvent paymentFailedEvent = new PaymentFailedEvent(
                        command.getOrderId(), command.getTotalPrice(), command.getFoodItemReservationList());
                String eventStr = objectMapper.writeValueAsString(paymentFailedEvent);
                //send through producerRecord with headers()
                ProducerRecord<String, Object> record = new ProducerRecord<>(paymentEventTopic, eventStr);
                record.headers().add(new RecordHeader("payment-event", "payment-failed-event".getBytes(StandardCharsets.UTF_8)));
                kafkaTemplate.send(record);
                //send to Kafka broker
//                kafkaTemplate.send(paymentEventTopic, eventStr);
                LOGGER.info("Payment Failed Event: {}", paymentFailedEvent.toString());
            }
        }
    }
}
