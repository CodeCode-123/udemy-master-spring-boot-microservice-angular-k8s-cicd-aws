package com.codecode.foodcatalogue.service.impl;

import com.codecode.core.dto.FoodItemDTO;
import com.codecode.core.dto.FoodItemReservation;
import com.codecode.foodcatalogue.entity.FoodItem;
import com.codecode.foodcatalogue.entity.FoodReservation;
import com.codecode.foodcatalogue.repository.FoodItemRepo;
import com.codecode.foodcatalogue.repository.FoodReservationRepo;
import com.codecode.foodcatalogue.service.FoodReservationService;
import jakarta.ws.rs.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class FoodReservationServiceImpl implements FoodReservationService {
    private final FoodReservationRepo foodReservationRepo;
    private final FoodItemRepo foodItemRepo;
    private static final Logger LOGGER = LoggerFactory.getLogger(FoodReservationServiceImpl.class);

    public FoodReservationServiceImpl(FoodReservationRepo foodReservationRepo, FoodItemRepo foodItemRepo) {
        this.foodReservationRepo = foodReservationRepo;
        this.foodItemRepo = foodItemRepo;
    }

    @Override
    //@Transactional
    public List<FoodReservation> reserve(List<FoodItemDTO> foodItemDTOList, Integer orderId) {
        List<FoodReservation> foodReservationList = new ArrayList<>();
        for (FoodItemDTO foodItemDTO: foodItemDTOList) {
            int foodItemId = foodItemDTO.getId();
            Optional<FoodItem> foodItemOptional = foodItemRepo.findById(foodItemId);
            if (foodItemOptional.isEmpty()) {
                throw new NotFoundException("FoodItem is not found by foodItemId: " + foodItemId);
            }
            FoodReservation foodReservation = convertFoodItemDTOToFoodReservation(foodItemDTO);
            foodReservation.setVeg(foodItemOptional.get().isVeg());
            foodReservation.setOrderId(orderId);
            foodReservationList.add(foodReservation);
        }
        foodReservationRepo.saveAll(foodReservationList);
        return foodReservationList;
    }

    @Override
    @Transactional
    public void cancelReservation(Integer orderId) {
        List<FoodReservation> foodReservationList = foodReservationRepo.findByOrderId(orderId);
        if (foodReservationList.isEmpty()) {
            throw new NotFoundException("Food Reservation list is not found by orderId: " + orderId);
        }
        foodReservationRepo.deleteByOrderId(orderId);
    }

    @Override
    @Transactional
    public void cancelReservation(Integer orderId, List<FoodItemReservation> foodItemReservationList) {
        List<FoodReservation> foodReservationListDB = foodReservationRepo.findByOrderId(orderId);
        if (foodReservationListDB.isEmpty()) {
            throw new NotFoundException("Food Reservation list is not found by orderId: " + orderId);
        }
        if (isSameFood(foodReservationListDB, foodItemReservationList)) {
            LOGGER.info("The cancel reservation command contains the same food reservation list as the record");
        } else {
            LOGGER.info("The cancel reservation command contains the different food reservation list as the record: {}", foodItemReservationList);
        }
        foodReservationRepo.deleteByOrderId(orderId);
    }


    private FoodReservation convertFoodItemDTOToFoodReservation(FoodItemDTO foodItemDTO) {
        FoodReservation foodReservation = new FoodReservation();
        foodReservation.setFoodItemId(foodItemDTO.getId());
        foodReservation.setItemName(foodItemDTO.getItemName());
        foodReservation.setPrice(foodItemDTO.getPrice());
        foodReservation.setQuantity(foodItemDTO.getQuantity());
        foodReservation.setRestaurantId(foodItemDTO.getRestaurantId());
        return foodReservation;
    }

    private boolean isSameFood(List<FoodReservation> foodReservationList, List<FoodItemReservation> foodItemReservationList) {
        Map<String, Integer> mapReservation = new HashMap<>();
        for (FoodReservation foodReservation: foodReservationList) {
            mapReservation.put("FoodItemId", foodReservation.getFoodItemId());
            mapReservation.put("Quantity", foodReservation.getQuantity());
        }
        Map<String, Integer> mapItem = new HashMap<>();
        for (FoodItemReservation foodItemReservation: foodItemReservationList) {
            mapItem.put("FoodItemId", foodItemReservation.getFoodItemId());
            mapItem.put("Quantity", foodItemReservation.getQuantity());
        }
        return mapReservation.equals(mapItem);
    }
}
