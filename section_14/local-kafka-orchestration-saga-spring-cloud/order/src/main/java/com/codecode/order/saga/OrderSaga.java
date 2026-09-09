package com.codecode.order.saga;

import com.codecode.core.dto.FoodItemDTO;
import com.codecode.core.dto.FoodItemReservation;
import com.codecode.core.dto.OrderDTO;
import com.codecode.core.dto.command.*;
import com.codecode.core.dto.event.*;
import com.codecode.core.types.OrderStatus;
import com.codecode.order.entity.Order;
import com.codecode.order.entity.OrderHistory;
import com.codecode.order.service.OrderHistoryService;
import com.codecode.order.service.OrderService;
import com.codecode.order.service.SequenceGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

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

    public OrderSaga(KafkaTemplate<String, Object> kafkaTemplate,
                     OrderHistoryService orderHistoryService, OrderService orderService) {
        this.kafkaTemplate = kafkaTemplate;
        this.orderHistoryService = orderHistoryService;
        this.orderService = orderService;
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
    public void handleEvent(FoodReservedEvent event) {
        LOGGER.info("Food Reserved Event: {}", event.toString());
        ProcessPaymentCommand processPaymentCommand = new ProcessPaymentCommand();
        List<FoodItemReservation> foodItemReservationList = event.getFoodItemReservationList();
        double totalPrice = 0;
        for (FoodItemReservation foodItemReservation: foodItemReservationList) {
            double price = foodItemReservation.getPrice();
            int qty = foodItemReservation.getQuantity();
            totalPrice += price * qty;
        }
        processPaymentCommand.setFoodItemReservationList(foodItemReservationList);
        processPaymentCommand.setOrderId(foodItemReservationList.getFirst().getOrderId());
        processPaymentCommand.setTotalPrice(totalPrice);
        kafkaTemplate.send(paymentCommandTopic, processPaymentCommand);
    }

    @KafkaListener(topics = "${app.kafka.food-event-topic}", groupId = "order-ms-2")
    public void handleEvent(FoodReservationFailedEvent event) {
        LOGGER.info("Food Reservation Failed: {}", event.toString());
    }

    @KafkaListener(topics = "${app.kafka.payment-event-topic}", groupId = "order-ms-1")
    public void handleEvent(PaymentProcessedEvent paymentProcessedEvent) {
        LOGGER.info("Payment Processed Event: {}", paymentProcessedEvent);
        ApproveOrderCommand approveOrderCommand = new ApproveOrderCommand(paymentProcessedEvent.getOrderId());
        kafkaTemplate.send(orderCommandTopic, approveOrderCommand);
    }

    @KafkaListener(topics = "${app.kafka.payment-event-topic}", groupId = "order-ms-2")
    public void handleEvent(PaymentFailedEvent paymentFailedEvent) {
        LOGGER.info("Payment Failed Event: {}", paymentFailedEvent.toString());
        List<FoodItemReservation> foodItemReservationList = paymentFailedEvent.getFoodItemReservationList();
        CancelFoodReservationCommand cancelFoodReservationCommand = new CancelFoodReservationCommand();
        cancelFoodReservationCommand.setOrderId(paymentFailedEvent.getOrderId());
        cancelFoodReservationCommand.setFoodItemReservationList(foodItemReservationList);
        kafkaTemplate.send(foodCommandTopic, cancelFoodReservationCommand);
    }

    @KafkaListener(topics = "${app.kafka.order-event-topic}", groupId = "order-ms-2")
    public void handleEvent(OrderApprovedEvent orderApprovedEvent) {
        LOGGER.info("Order Approved Event: {}", orderApprovedEvent);
        //save to the order history document
        orderHistoryService.add(orderApprovedEvent.getOrderId(), OrderStatus.APPROVED);
        //retrieve Order from the database
        OrderDTO orderDTO = orderService.getOrderByOrderId(orderApprovedEvent.getOrderId());
        List<FoodItemDTO> foodItemDTOList = orderDTO.getFoodItemsList();
        OrderApprovalMessageCommand command = new OrderApprovalMessageCommand();
        command.setOrderId(orderApprovedEvent.getOrderId());
        command.setFoodItemDTOList(orderDTO.getFoodItemsList());
        command.setUserDTO(orderDTO.getUserDTO());
        //send message command to message service
        kafkaTemplate.send(messageCommandTopic, command);
    }

    @KafkaListener(topics = "${app.kafka.order-event-topic}", groupId = "order-ms-3")
    public void handleEvent(OrderRejectedEvent orderRejectedEvent) {
        OrderDTO orderDTO = orderService.getOrderByOrderId(orderRejectedEvent.getOrderId());
        List<FoodItemDTO> foodItemDTOList = orderDTO.getFoodItemsList();
        OrderRejectionMessageCommand command = new OrderRejectionMessageCommand();
        command.setOrderId(orderRejectedEvent.getOrderId());
        command.setFoodItemDTOList(orderDTO.getFoodItemsList());
        command.setUserDTO(orderDTO.getUserDTO());
        //send message command to message service
        kafkaTemplate.send(messageCommandTopic, command);
    }

    @KafkaListener(topics = "${app.kafka.food-event-topic}", groupId = "order-ms-3")
    public void handleEvent(FoodReservationCancelledEvent event) {
        LOGGER.info("Food Reservation Cancelled Event: {}", event.toString());
        RejectOrderCommand rejectOrderCommand = new RejectOrderCommand(event.getOrderId());
        kafkaTemplate.send(orderCommandTopic, rejectOrderCommand);
        orderHistoryService.add(event.getOrderId(), OrderStatus.REJECTED);

    }
}
