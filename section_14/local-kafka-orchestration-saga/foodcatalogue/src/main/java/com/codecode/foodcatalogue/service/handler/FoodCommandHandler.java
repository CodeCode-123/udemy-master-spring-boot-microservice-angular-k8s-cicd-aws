package com.codecode.foodcatalogue.service.handler;

import com.codecode.core.dto.command.ReserveFoodCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@KafkaListener(topics="${app.kafka.food-command-topic}")
public class FoodCommandHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(FoodCommandHandler.class);

    @KafkaHandler
    public void handleCommand(ReserveFoodCommand reserveFoodCommand) {
        LOGGER.info("ReserveFoodCommand: orderId: {}, reserveFoodCommand: {}", reserveFoodCommand.getOrderId(), reserveFoodCommand.toString());
    }
}
