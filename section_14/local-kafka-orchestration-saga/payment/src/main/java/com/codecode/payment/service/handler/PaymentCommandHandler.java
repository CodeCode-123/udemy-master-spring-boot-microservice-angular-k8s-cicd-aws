package com.codecode.payment.service.handler;

import com.codecode.core.dto.command.ProcessPaymentCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics={"${app.kafka.payment-command-topic}"})
public class PaymentCommandHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentCommandHandler.class);

    @KafkaHandler
    public void handler(ProcessPaymentCommand command) {
        LOGGER.info("Process Payment Command: {}", command.getProcessPaymentCommandToString());
    }
}
