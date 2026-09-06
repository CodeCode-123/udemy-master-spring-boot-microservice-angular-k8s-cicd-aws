package com.codecode.order.saga;

import com.codecode.core.dto.command.ReserveFoodCommand;
import com.codecode.core.dto.event.OrderCreatedEvent;
import com.codecode.core.types.OrderStatus;
import com.codecode.order.entity.OrderHistory;
import com.codecode.order.service.OrderHistoryService;
import com.codecode.order.service.SequenceGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

//a list of @KafkaHandler will receive different events, and then send different commands
@Component
public class OrderSaga {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderSaga.class);

    @Value("${app.kafka.food-command-topic}")
    private String foodCommandTopic;

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final OrderHistoryService orderHistoryService;

    public OrderSaga(ObjectMapper objectMapper, KafkaTemplate<String, String> kafkaTemplate,
                     OrderHistoryService orderHistoryService) {
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
        this.orderHistoryService = orderHistoryService;
    }

    @KafkaListener(topics = "order-event", groupId = "order-ms")
    public void getOrderCreatedEvent(String s) {
        LOGGER.info("Order Created Event: " + s);
        OrderCreatedEvent event = objectMapper.readValue(s, OrderCreatedEvent.class);
        ReserveFoodCommand reserveFoodCommand = new ReserveFoodCommand(event.getOrderId(),
                event.getFoodItemsList());
        kafkaTemplate.send(foodCommandTopic, objectMapper.writeValueAsString(reserveFoodCommand));
        orderHistoryService.saveOrderHistoryInDb(event);
    }
}
