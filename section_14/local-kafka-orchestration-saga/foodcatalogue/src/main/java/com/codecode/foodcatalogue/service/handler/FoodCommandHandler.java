package com.codecode.foodcatalogue.service.handler;

import com.codecode.core.dto.command.ReserveFoodCommand;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class FoodCommandHandler {

    public void handleCommand(ReserveFoodCommand reserveFoodCommand) {

    }
}
