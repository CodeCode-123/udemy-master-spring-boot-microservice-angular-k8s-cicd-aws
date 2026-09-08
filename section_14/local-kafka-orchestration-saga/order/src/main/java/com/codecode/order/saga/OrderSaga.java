package com.codecode.order.saga;

import com.codecode.core.dto.FoodItemReservation;
import com.codecode.core.dto.command.ProcessPaymentCommand;
import com.codecode.core.dto.command.ReserveFoodCommand;
import com.codecode.core.dto.event.FoodReservationFailedEvent;
import com.codecode.core.dto.event.FoodReservedEvent;
import com.codecode.core.dto.event.OrderCreatedEvent;
import com.codecode.core.types.OrderStatus;
import com.codecode.order.entity.OrderHistory;
import com.codecode.order.service.OrderHistoryService;
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

import java.util.List;

//a list of @KafkaHandler will receive different events, and then send different commands
@Component
@KafkaListener(topics = {
        "${app.kafka.order-event-topic}",
        "${app.kafka.food-event-topic}"
})
public class OrderSaga {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderSaga.class);

    @Value("${app.kafka.food-command-topic}")
    private String foodCommandTopic;

    @Value("${app.kafka.payment-command-topic}")
    private String paymentCommandTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OrderHistoryService orderHistoryService;

    public OrderSaga(KafkaTemplate<String, Object> kafkaTemplate,
                     OrderHistoryService orderHistoryService) {
        this.kafkaTemplate = kafkaTemplate;
        this.orderHistoryService = orderHistoryService;
    }

    @KafkaHandler
    public void handleEvent(OrderCreatedEvent orderCreatedEvent) {
        LOGGER.info("Order Created Event: orderId: {}, orderCreatedEvent: {}", orderCreatedEvent.getOrderId(), orderCreatedEvent.toString());
        ReserveFoodCommand reserveFoodCommand = new ReserveFoodCommand(orderCreatedEvent.getOrderId(),
                orderCreatedEvent.getFoodItemsList());
        kafkaTemplate.send(foodCommandTopic, reserveFoodCommand);
        orderHistoryService.saveOrderHistoryInDb(orderCreatedEvent);
    }

    @KafkaHandler
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

    @KafkaHandler
    public void handleEvent(FoodReservationFailedEvent event) {
        LOGGER.info("Food Reservation Failed: {}", event.toString());
//        System.out.println("Food Reservation Failed: " + event.getFoodReservationFailedEventToString());
    }
}
