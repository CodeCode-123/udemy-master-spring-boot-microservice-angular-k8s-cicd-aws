package com.codecode.foodcatalogue.service.handler;


import com.codecode.foodcatalogue.dto.FoodItemDTO;
import com.codecode.foodcatalogue.dto.FoodItemReservation;
import com.codecode.foodcatalogue.dto.command.CancelFoodReservationCommand;
import com.codecode.foodcatalogue.dto.command.ReserveFoodCommand;
import com.codecode.foodcatalogue.dto.event.FoodReservationCancelledEvent;
import com.codecode.foodcatalogue.dto.event.FoodReservationFailedEvent;
import com.codecode.foodcatalogue.dto.event.FoodReservedEvent;
import com.codecode.foodcatalogue.entity.FoodReservation;
import com.codecode.foodcatalogue.service.FoodReservationService;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
//@KafkaListener(topics="${app.kafka.food-command-topic}")
public class FoodCommandHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(FoodCommandHandler.class);
    private final FoodReservationService foodReservationService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String foodEventTopic;
    private final ObjectMapper objectMapper;

    public FoodCommandHandler(FoodReservationService foodReservationService, KafkaTemplate<String, Object> kafkaTemplate,
                              @Value("${app.kafka.food-event-topic}") String foodEventTopic, ObjectMapper objectMapper) {
        this.foodReservationService = foodReservationService;
        this.kafkaTemplate = kafkaTemplate;
        this.foodEventTopic = foodEventTopic;
        this.objectMapper = objectMapper;
    }

    //@Transactional
    //@KafkaHandler
    @KafkaListener(topics="${app.kafka.food-command-topic}", groupId = "foodcatalogue-ms-1")
    public void handleCommand(ReserveFoodCommand reserveFoodCommand) {
        LOGGER.info("ReserveFoodCommand: orderId: {}, reserveFoodCommand: {}", reserveFoodCommand.getOrderId(), reserveFoodCommand.toString());
        List<FoodItemDTO> foodItemDTOList = reserveFoodCommand.getFoodItemDTOList();
        Integer orderId = reserveFoodCommand.getOrderId();
        List<FoodItemReservation> foodItemReservationList = new ArrayList<>();
        for (FoodItemDTO foodItemDTO: foodItemDTOList) {
            FoodItemReservation foodItemReservation = convertToFoodItemReservation(foodItemDTO);
            foodItemReservation.setOrderId(orderId);
            foodItemReservationList.add(foodItemReservation);
        }

        try {
            //save to the database
            List<FoodReservation> foodReservationList = foodReservationService.reserve(foodItemDTOList, orderId);
            FoodReservedEvent foodReservedEvent = new FoodReservedEvent(foodItemReservationList);
            //send to the Kafka broker
            String eventStr = objectMapper.writeValueAsString(foodReservedEvent);
            //send ProducerRecord with headers
            ProducerRecord<String, Object> record = new ProducerRecord<>(foodEventTopic, eventStr);
            record.headers().add("food-event", "food-reserved-event".getBytes(StandardCharsets.UTF_8));
            kafkaTemplate.send(record);
//            kafkaTemplate.send(foodEventTopic, eventStr);
            LOGGER.info("Food Reserved: {}", foodReservedEvent.getFoodReservedEventToString());
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            FoodReservationFailedEvent foodReservationFailedEvent = new FoodReservationFailedEvent(foodItemReservationList);
            String eventStr = objectMapper.writeValueAsString(foodReservationFailedEvent);
            //send ProducerRecord with headers
            ProducerRecord<String, Object> record = new ProducerRecord<>(foodEventTopic, eventStr);
            record.headers().add("food-event", "food-reservation-failed-event".getBytes(StandardCharsets.UTF_8));
            kafkaTemplate.send(record);
//            kafkaTemplate.send(foodEventTopic, eventStr);
            LOGGER.info("Food Reservation Failed: {}", foodReservationFailedEvent.getFoodReservationFailedEventToString());
        }
    }

    @KafkaListener(topics="${app.kafka.food-command-topic}", groupId = "foodcatalogue-ms-2")
    public void handleCancelFoodReservationCommand(String commandStr/*CancelFoodReservationCommand command*/) {
        CancelFoodReservationCommand command = objectMapper.readValue(commandStr, CancelFoodReservationCommand.class);
        LOGGER.info("CancelFoodReservationCommand: {}", command.toString());
        List<FoodItemReservation> foodItemReservationList = command.getFoodItemReservationList();
        Integer orderId = command.getOrderId();
        foodReservationService.cancelReservation(orderId, foodItemReservationList);
        FoodReservationCancelledEvent foodReservationCancelledEvent = new FoodReservationCancelledEvent();
        foodReservationCancelledEvent.setOrderId(orderId);
        foodReservationCancelledEvent.setFoodItemReservationList(foodItemReservationList);
        String eventStr = objectMapper.writeValueAsString(foodReservationCancelledEvent);
        //send ProducerRecord with headers
        ProducerRecord<String, Object> record = new ProducerRecord<>(foodEventTopic, eventStr);
        record.headers().add("food-event", "food-reservation-cancelled-event".getBytes(StandardCharsets.UTF_8));
        kafkaTemplate.send(record);
        //send to OrderSaga
//        kafkaTemplate.send(foodEventTopic, eventStr);
    }

    private FoodItemReservation convertToFoodItemReservation(FoodItemDTO foodItemDTO) {
        FoodItemReservation foodItemReservation = new FoodItemReservation();
        foodItemReservation.setFoodItemId(foodItemDTO.getId());
        foodItemReservation.setPrice(foodItemDTO.getPrice());
        foodItemReservation.setQuantity(foodItemDTO.getQuantity());
        return foodItemReservation;
    }

//    //method for test
//    private void throwException() {
//        throw new RuntimeException();
//    }
}
