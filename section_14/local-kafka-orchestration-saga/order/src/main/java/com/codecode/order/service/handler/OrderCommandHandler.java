package com.codecode.order.service.handler;

import com.codecode.core.dto.command.ApproveOrderCommand;
import com.codecode.core.dto.command.RejectOrderCommand;
import com.codecode.core.dto.event.OrderApprovedEvent;
import com.codecode.core.dto.event.PaymentProcessedEvent;
import com.codecode.order.entity.Order;
import com.codecode.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderCommandHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderCommandHandler.class);
    private final OrderService orderService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.order-event-topic}")
    private String orderEventTopic;

    public OrderCommandHandler(OrderService orderService, KafkaTemplate<String, Object> kafkaTemplate) {
        this.orderService = orderService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = {"${app.kafka.order-command-topic}"}, groupId = "order-ms-1")
    public void handleCommand(ApproveOrderCommand command) {
        //LOGGER.info("Approve Order Command: {}", command.toString());
        Order order = orderService.approveOrder(command.getOrderId());
        LOGGER.info("Approved Order: Order Id: {}, Order Status: {}", order.getOrderId(), order.getOrderStatus());
        OrderApprovedEvent orderApprovedEvent = new OrderApprovedEvent(command.getOrderId());
        kafkaTemplate.send(orderEventTopic, orderApprovedEvent);
    }

    @KafkaListener(topics = {"${app.kafka.order-command-topic}"}, groupId = "order-ms-2")
    public void handleCommand(RejectOrderCommand command) {
        //LOGGER.info("Reject Order Command: {}", command.toString());
        Order order = orderService.rejectOrder(command.getOrderId());
        LOGGER.info("Rejected Order: Order Id: {}, Order Status: {}", order.getOrderId(), order.getOrderStatus());
    }

}
