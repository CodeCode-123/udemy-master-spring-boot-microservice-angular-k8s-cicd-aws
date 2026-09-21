package com.codecode.order.service.handler;


import com.codecode.order.dto.command.ApproveOrderCommand;
import com.codecode.order.dto.command.RejectOrderCommand;
import com.codecode.order.dto.event.OrderApprovedEvent;
import com.codecode.order.dto.event.OrderRejectedEvent;
import com.codecode.order.entity.Order;
import com.codecode.order.service.OrderService;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

@Component
public class OrderCommandHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderCommandHandler.class);
    private final OrderService orderService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.order-event-topic}")
    private String orderEventTopic;

    public OrderCommandHandler(OrderService orderService, KafkaTemplate<String, Object> kafkaTemplate, ObjectMapper objectMapper) {
        this.orderService = orderService;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = {"${app.kafka.order-command-topic}"}, groupId = "order-ms-1")
    public void handleOrderCommand(@Payload String commandStr, @Header(name="order-command", required = false) String orderCommand/*ApproveOrderCommand command*/) {
        LOGGER.info("Order Command: {}", commandStr);
        if ("approve-order-command".equals(orderCommand)) {
            ApproveOrderCommand command = objectMapper.readValue(commandStr, ApproveOrderCommand.class);
            //LOGGER.info("Approve Order Command: {}", command.toString());
            Order order = orderService.approveOrder(command.getOrderId());
            LOGGER.info("Approved Order: Order Id: {}, Order Status: {}", order.getOrderId(), order.getOrderStatus());
            OrderApprovedEvent orderApprovedEvent = new OrderApprovedEvent(command.getOrderId());
            String eventStr = objectMapper.writeValueAsString(orderApprovedEvent);
            //send through producerRecord with headers()
            ProducerRecord<String, Object> record = new ProducerRecord<>(orderEventTopic, eventStr);
            record.headers().add(new RecordHeader("order-event", "order-approved-event".getBytes(StandardCharsets.UTF_8)));
            kafkaTemplate.send(record);
//            kafkaTemplate.send(orderEventTopic, eventStr);
        } else if ("reject-order-command".equals(orderCommand)) {
            RejectOrderCommand command = objectMapper.readValue(commandStr, RejectOrderCommand.class);
            //LOGGER.info("Reject Order Command: {}", command.toString());
            Order order = orderService.rejectOrder(command.getOrderId());
            LOGGER.info("Rejected Order: Order Id: {}, Order Status: {}", order.getOrderId(), order.getOrderStatus());
            OrderRejectedEvent orderRejectedEvent = new OrderRejectedEvent(command.getOrderId());
            String eventStr = objectMapper.writeValueAsString(orderRejectedEvent);
            //send through producerRecord with headers()
            ProducerRecord<String, Object> record = new ProducerRecord<>(orderEventTopic, eventStr);
            record.headers().add(new RecordHeader("order-event", "order-rejected-event".getBytes(StandardCharsets.UTF_8)));
            kafkaTemplate.send(record);
//            kafkaTemplate.send(orderEventTopic, eventStr);
        }
    }

//    @KafkaListener(topics = {"${app.kafka.order-command-topic}"}, groupId = "order-ms-2")
//    public void handleRejectOrderCommand(String commandStr/*RejectOrderCommand command*/) {
//        RejectOrderCommand command = objectMapper.readValue(commandStr, RejectOrderCommand.class);
//        //LOGGER.info("Reject Order Command: {}", command.toString());
//        Order order = orderService.rejectOrder(command.getOrderId());
//        LOGGER.info("Rejected Order: Order Id: {}, Order Status: {}", order.getOrderId(), order.getOrderStatus());
//        OrderRejectedEvent orderRejectedEvent = new OrderRejectedEvent(command.getOrderId());
//        String eventStr = objectMapper.writeValueAsString(orderRejectedEvent);
//        kafkaTemplate.send(orderEventTopic, eventStr);
//    }
}
