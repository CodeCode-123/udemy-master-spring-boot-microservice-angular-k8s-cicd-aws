package com.codecode.foodcatalogue.service.handler;

import com.codecode.core.dto.FoodItemDTO;
import com.codecode.core.dto.FoodItemReservation;
import com.codecode.core.dto.command.ReserveFoodCommand;
import com.codecode.core.dto.event.FoodReservationFailedEvent;
import com.codecode.core.dto.event.FoodReservedEvent;
import com.codecode.foodcatalogue.entity.FoodReservation;
import com.codecode.foodcatalogue.service.FoodReservationService;
import jakarta.ws.rs.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@KafkaListener(topics="${app.kafka.food-command-topic}")
public class FoodCommandHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(FoodCommandHandler.class);
    private final FoodReservationService foodReservationService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String foodEventTopic;

    public FoodCommandHandler(FoodReservationService foodReservationService, KafkaTemplate<String, Object> kafkaTemplate,
                              @Value("${app.kafka.food-event-topic}") String foodEventTopic) {
        this.foodReservationService = foodReservationService;
        this.kafkaTemplate = kafkaTemplate;
        this.foodEventTopic = foodEventTopic;
    }

    //@Transactional
    @KafkaHandler
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
            //Test with different condition
            //throwException();
            //save to the database
            List<FoodReservation> foodReservationList = foodReservationService.reserve(foodItemDTOList, orderId);
            FoodReservedEvent foodReservedEvent = new FoodReservedEvent(foodItemReservationList);
            //send to the Kafka broker
            kafkaTemplate.send(foodEventTopic, foodReservedEvent);
            LOGGER.info("Food Reserved: {}", foodReservedEvent.getFoodReservedEventToString());
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            FoodReservationFailedEvent foodReservationFailedEvent = new FoodReservationFailedEvent(foodItemReservationList);
            kafkaTemplate.send(foodEventTopic, foodReservationFailedEvent);
            LOGGER.info("Food Reservation Failed: {}", foodReservationFailedEvent.getFoodReservationFailedEventToString());
        }
    }


    private FoodItemReservation convertToFoodItemReservation(FoodItemDTO foodItemDTO) {
        FoodItemReservation foodItemReservation = new FoodItemReservation();
        foodItemReservation.setFoodItemId(foodItemDTO.getId());
        foodItemReservation.setPrice(foodItemDTO.getPrice());
        foodItemReservation.setQuantity(foodItemDTO.getQuantity());
        return foodItemReservation;
    }

    private void throwException() {
        throw new NotFoundException();
    }
}
