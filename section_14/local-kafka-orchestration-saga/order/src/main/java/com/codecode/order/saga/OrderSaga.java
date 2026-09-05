package com.codecode.order.saga;

import com.codecode.core.dto.OrderDTO;
import com.codecode.core.dto.event.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.expression.spel.ast.OpDec;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

//a list of @KafkaHandler will receive different events, and then send different commands
@Component
public class OrderSaga {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderSaga.class);

    private final ObjectMapper objectMapper;

    public OrderSaga(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "order-event", groupId = "order-ms")
    public OrderCreatedEvent getOrderCreatedEvent(String s) {
        LOGGER.info("Order Created Event: " + s);
        OrderCreatedEvent orderCreatedEvent = objectMapper.readValue(s, OrderCreatedEvent.class);
        return orderCreatedEvent;
    }
}
