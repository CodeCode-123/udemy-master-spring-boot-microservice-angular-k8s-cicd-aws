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

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OrderHistoryService orderHistoryService;

    public OrderSaga(KafkaTemplate<String, Object> kafkaTemplate,
                     OrderHistoryService orderHistoryService) {
        this.kafkaTemplate = kafkaTemplate;
        this.orderHistoryService = orderHistoryService;
    }

    @KafkaListener(topics = "order-event", groupId = "order-ms")
    public void getOrderCreatedEvent(OrderCreatedEvent orderCreatedEvent) {
        LOGGER.info("Order Created Event: orderId: {}, orderCreatedEvent: {}", orderCreatedEvent.getOrderId(), orderCreatedEvent.toString());
        ReserveFoodCommand reserveFoodCommand = new ReserveFoodCommand(orderCreatedEvent.getOrderId(),
                orderCreatedEvent.getFoodItemsList());
        kafkaTemplate.send(foodCommandTopic, reserveFoodCommand);
        orderHistoryService.saveOrderHistoryInDb(orderCreatedEvent);
    }
}
