package com.codecode.order.saga;


import com.codecode.order.dto.FoodItemDTO;
import com.codecode.order.dto.FoodItemReservation;
import com.codecode.order.dto.OrderDTO;
import com.codecode.order.dto.command.*;
import com.codecode.order.dto.event.*;
import com.codecode.order.dto.types.OrderStatus;
import com.codecode.order.service.OrderHistoryService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//a list of @KafkaHandler will receive different events, and then send different commands
@Component
//@KafkaListener(topics = {
//        "${app.kafka.order-event-topic}",
//        "${app.kafka.food-event-topic}"
//})
public class OrderSaga {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderSaga.class);

    @Value("${app.kafka.food-command-topic}")
    private String foodCommandTopic;

    @Value("${app.kafka.payment-command-topic}")
    private String paymentCommandTopic;

    @Value("${app.kafka.order-command-topic}")
    private String orderCommandTopic;

    @Value("${app.kafka.message-command-topic}")
    private String messageCommandTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OrderHistoryService orderHistoryService;
    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    public OrderSaga(KafkaTemplate<String, Object> kafkaTemplate,
                     OrderHistoryService orderHistoryService, OrderService orderService, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.orderHistoryService = orderHistoryService;
        this.orderService = orderService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${app.kafka.order-event-topic}", groupId = "order-ms-1")
    //@KafkaHandler
    public void handleEvent(OrderCreatedEvent orderCreatedEvent) {
        LOGGER.info("Order Created Event: orderId: {}, orderCreatedEvent: {}", orderCreatedEvent.getOrderId(), orderCreatedEvent.toString());
        ReserveFoodCommand reserveFoodCommand = new ReserveFoodCommand(orderCreatedEvent.getOrderId(),
                orderCreatedEvent.getFoodItemsList());
        kafkaTemplate.send(foodCommandTopic, reserveFoodCommand);
        orderHistoryService.saveOrderHistoryInDb(orderCreatedEvent);
    }

    //annotated with the topics to the method
    @KafkaListener(topics = "${app.kafka.food-event-topic}", groupId = "order-ms-1")
    public void handleFoodEvent(@Payload String eventStr, @Header(name="food-event", required = false) String foodEvent/*FoodReservedEvent event*/) {
        LOGGER.info("Food Event: {}", eventStr);
        if ("food-reserved-event".equals(foodEvent)) {
            FoodReservedEvent event = objectMapper.readValue(eventStr, FoodReservedEvent.class);
            ProcessPaymentCommand processPaymentCommand = new ProcessPaymentCommand();
            List<FoodItemReservation> foodItemReservationList = event.getFoodItemReservationList();
            double totalPrice = 0;
            for (FoodItemReservation foodItemReservation: foodItemReservationList) {
                double price = foodItemReservation.getPrice();
                int qty = foodItemReservation.getQuantity();
                totalPrice += price * qty;
            }
            processPaymentCommand.setFoodItemReservationList(foodItemReservationList);
            processPaymentCommand.setOrderId(foodItemReservationList.get(0).getOrderId());
            processPaymentCommand.setTotalPrice(totalPrice);
            String commandStr = objectMapper.writeValueAsString(processPaymentCommand);
            //send through producerRecord with headers()
            ProducerRecord<String, Object> record = new ProducerRecord<>(paymentCommandTopic, commandStr);
            record.headers().add(new RecordHeader("payment-command", "process-payment-command".getBytes(StandardCharsets.UTF_8)));
            kafkaTemplate.send(record);
        } else if ("food-reservation-failed-event".equals(foodEvent)) {
            FoodReservationFailedEvent event = objectMapper.readValue(eventStr, FoodReservationFailedEvent.class);
            LOGGER.info("Food Reservation Failed: {}", event.toString());
        } else if ("food-reservation-cancelled-event".equals(foodEvent)) {
            FoodReservationCancelledEvent event = objectMapper.readValue(eventStr, FoodReservationCancelledEvent.class);
            LOGGER.info("Food Reservation Cancelled Event: {}", event.toString());
            RejectOrderCommand rejectOrderCommand = new RejectOrderCommand(event.getOrderId());
            String commandStr = objectMapper.writeValueAsString(rejectOrderCommand);
            //send through producerRecord with headers()
            ProducerRecord<String, Object> record = new ProducerRecord<>(orderCommandTopic, commandStr);
            record.headers().add(new RecordHeader("order-command", "reject-order-command".getBytes(StandardCharsets.UTF_8)));
            kafkaTemplate.send(record);
//            kafkaTemplate.send(orderCommandTopic, commandStr);
            orderHistoryService.add(event.getOrderId(), OrderStatus.REJECTED);
        }
    }

//    @KafkaListener(topics = "${app.kafka.food-event-topic}", groupId = "order-ms-2")
//    public void handleFoodReservationFailedEvent(String eventStr/*FoodReservationFailedEvent event*/) {
//        FoodReservationFailedEvent event = objectMapper.readValue(eventStr, FoodReservationFailedEvent.class);
//        LOGGER.info("Food Reservation Failed: {}", event.toString());
//    }

    @KafkaListener(topics = "${app.kafka.payment-event-topic}", groupId = "order-ms-1")
    public void handlePaymentEvent(@Payload String eventStr, @Header(name="payment-event", required = false) String paymentEvent/*PaymentProcessedEvent paymentProcessedEvent*/) {
        LOGGER.info("payment event: {}", eventStr);
        if ("payment-processed-event".equals(paymentEvent)) {
            PaymentProcessedEvent paymentProcessedEvent = objectMapper.readValue(eventStr, PaymentProcessedEvent.class);
            LOGGER.info("Payment Processed Event: {}", paymentProcessedEvent);
            ApproveOrderCommand approveOrderCommand = new ApproveOrderCommand(paymentProcessedEvent.getOrderId());
            String commandStr = objectMapper.writeValueAsString(approveOrderCommand);
            //send through producerRecord with headers()
            ProducerRecord<String, Object> record = new ProducerRecord<>(orderCommandTopic, commandStr);
            record.headers().add(new RecordHeader("order-command", "approve-order-command".getBytes(StandardCharsets.UTF_8)));
            kafkaTemplate.send(record);
//            kafkaTemplate.send(orderCommandTopic, commandStr);
        } else if ("payment-failed-event".equals(paymentEvent)) {
            PaymentFailedEvent paymentFailedEvent = objectMapper.readValue(eventStr, PaymentFailedEvent.class);
            LOGGER.info("Payment Failed Event: {}", paymentFailedEvent.toString());
            List<FoodItemReservation> foodItemReservationList = paymentFailedEvent.getFoodItemReservationList();
            CancelFoodReservationCommand cancelFoodReservationCommand = new CancelFoodReservationCommand();
            cancelFoodReservationCommand.setOrderId(paymentFailedEvent.getOrderId());
            cancelFoodReservationCommand.setFoodItemReservationList(foodItemReservationList);
            String commandStr = objectMapper.writeValueAsString(cancelFoodReservationCommand);
            //send through producerRecord with headers()
            ProducerRecord<String, Object> record = new ProducerRecord<>(foodCommandTopic, commandStr);
            record.headers().add(new RecordHeader("food-command", "cancel-food-reservation-command".getBytes(StandardCharsets.UTF_8)));
            kafkaTemplate.send(record);
//            kafkaTemplate.send(foodCommandTopic, commandStr);
        }
    }

//    @KafkaListener(topics = "${app.kafka.payment-event-topic}", groupId = "order-ms-2")
//    public void handlePaymentFailedEvent(String eventStr/*PaymentFailedEvent paymentFailedEvent*/) {
//        PaymentFailedEvent paymentFailedEvent = objectMapper.readValue(eventStr, PaymentFailedEvent.class);
//        LOGGER.info("Payment Failed Event: {}", paymentFailedEvent.toString());
//        List<FoodItemReservation> foodItemReservationList = paymentFailedEvent.getFoodItemReservationList();
//        CancelFoodReservationCommand cancelFoodReservationCommand = new CancelFoodReservationCommand();
//        cancelFoodReservationCommand.setOrderId(paymentFailedEvent.getOrderId());
//        cancelFoodReservationCommand.setFoodItemReservationList(foodItemReservationList);
//        String commandStr = objectMapper.writeValueAsString(cancelFoodReservationCommand);
//        kafkaTemplate.send(foodCommandTopic, commandStr);
//    }

    @KafkaListener(topics = "${app.kafka.order-event-topic}", groupId = "order-ms-2")
    public void handleOrderEvent(@Payload String eventStr, @Header(name="order-event", required = false) String orderEvent/*OrderApprovedEvent orderApprovedEvent*/) {
        LOGGER.info("Order Event: {}", eventStr);
        if ("order-approved-event".equals(orderEvent)) {
            OrderApprovedEvent orderApprovedEvent = objectMapper.readValue(eventStr, OrderApprovedEvent.class);

            //save to the order history document
            orderHistoryService.add(orderApprovedEvent.getOrderId(), OrderStatus.APPROVED);
            //retrieve Order from the database
            OrderDTO orderDTO = orderService.getOrderByOrderId(orderApprovedEvent.getOrderId());
            List<FoodItemDTO> foodItemDTOList = orderDTO.getFoodItemsList();
            OrderApprovalMessageCommand command = new OrderApprovalMessageCommand();
            command.setOrderId(orderApprovedEvent.getOrderId());
            command.setFoodItemDTOList(orderDTO.getFoodItemsList());
            command.setUserDTO(orderDTO.getUserDTO());
            String commandStr = objectMapper.writeValueAsString(command);
            //send through producerRecord with headers()
            ProducerRecord<String, Object> record = new ProducerRecord<>(messageCommandTopic, commandStr);
            record.headers().add(new RecordHeader("message-command", "order-approval-message-command".getBytes(StandardCharsets.UTF_8)));
            kafkaTemplate.send(record);
            //send message command to message service
//            kafkaTemplate.send(messageCommandTopic, commandStr);

        } else if ("order-rejected-event".equals(orderEvent)) {
            OrderRejectedEvent orderRejectedEvent = objectMapper.readValue(eventStr, OrderRejectedEvent.class);
            OrderDTO orderDTO = orderService.getOrderByOrderId(orderRejectedEvent.getOrderId());
            List<FoodItemDTO> foodItemDTOList = orderDTO.getFoodItemsList();
            OrderRejectionMessageCommand command = new OrderRejectionMessageCommand();
            command.setOrderId(orderRejectedEvent.getOrderId());
            command.setFoodItemDTOList(orderDTO.getFoodItemsList());
            command.setUserDTO(orderDTO.getUserDTO());
            String commandStr = objectMapper.writeValueAsString(command);
            //send through producerRecord with headers()
            ProducerRecord<String, Object> record = new ProducerRecord<>(messageCommandTopic, commandStr);
            record.headers().add(new RecordHeader("message-command", "order-rejection-message-command".getBytes(StandardCharsets.UTF_8)));
            kafkaTemplate.send(record);
            //send message command to message service
//            kafkaTemplate.send(messageCommandTopic, commandStr);

        }
    }

//    @KafkaListener(topics = "${app.kafka.order-event-topic}", groupId = "order-ms-3")
//    public void handleOrderRejectedEvent(String eventStr/*OrderRejectedEvent orderRejectedEvent*/) {
//        OrderRejectedEvent orderRejectedEvent = objectMapper.readValue(eventStr, OrderRejectedEvent.class);
//        OrderDTO orderDTO = orderService.getOrderByOrderId(orderRejectedEvent.getOrderId());
//        List<FoodItemDTO> foodItemDTOList = orderDTO.getFoodItemsList();
//        OrderRejectionMessageCommand command = new OrderRejectionMessageCommand();
//        command.setOrderId(orderRejectedEvent.getOrderId());
//        command.setFoodItemDTOList(orderDTO.getFoodItemsList());
//        command.setUserDTO(orderDTO.getUserDTO());
//        String commandStr = objectMapper.writeValueAsString(command);
//        //send message command to message service
//        kafkaTemplate.send(messageCommandTopic, commandStr);
//    }

//    @KafkaListener(topics = "${app.kafka.food-event-topic}", groupId = "order-ms-3")
//    public void handleFoodReservationCancelledEvent(String eventStr/*FoodReservationCancelledEvent event*/) {
//        FoodReservationCancelledEvent event = objectMapper.readValue(eventStr, FoodReservationCancelledEvent.class);
//        LOGGER.info("Food Reservation Cancelled Event: {}", event.toString());
//        RejectOrderCommand rejectOrderCommand = new RejectOrderCommand(event.getOrderId());
//        String commandStr = objectMapper.writeValueAsString(rejectOrderCommand);
//        kafkaTemplate.send(orderCommandTopic, commandStr);
//        orderHistoryService.add(event.getOrderId(), OrderStatus.REJECTED);
//    }
}
