package com.codecode.foodcatalogue.service.impl;

import com.codecode.core.dto.FoodItemDTO;
import com.codecode.foodcatalogue.entity.FoodItem;
import com.codecode.foodcatalogue.entity.FoodReservation;
import com.codecode.foodcatalogue.repository.FoodItemRepo;
import com.codecode.foodcatalogue.repository.FoodReservationRepo;
import com.codecode.foodcatalogue.service.FoodReservationService;
import jakarta.ws.rs.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FoodReservationServiceImpl implements FoodReservationService {
    private final FoodReservationRepo foodReservationRepo;
    private final FoodItemRepo foodItemRepo;

    public FoodReservationServiceImpl(FoodReservationRepo foodReservationRepo, FoodItemRepo foodItemRepo) {
        this.foodReservationRepo = foodReservationRepo;
        this.foodItemRepo = foodItemRepo;
    }

    @Override
    @Transactional
    public List<FoodReservation> reserve(List<FoodItemDTO> foodItemDTOList, Integer orderId) {
        List<FoodReservation> foodReservationList = new ArrayList<>();
        for (FoodItemDTO foodItemDTO: foodItemDTOList) {
            int foodItemId = foodItemDTO.getId();
            Optional<FoodItem> foodItemOptional = foodItemRepo.findById(foodItemId);
            System.out.println("Before loop");
            if (foodItemOptional.isEmpty()) {
                System.out.println("Not Found");
                throw new NotFoundException("FoodItem is not found by foodItemId: " + foodItemId);
            }
            System.out.println("After Loop");
            System.out.println(foodItemDTO.toString());
            FoodReservation foodReservation = convertFoodItemDTOToFoodReservation(foodItemDTO);
            foodReservation.setVeg(foodItemOptional.get().isVeg());
            System.out.println("After Convert to FoodReservation");
            foodReservation.setOrderId(orderId);
            System.out.println("foodReservation foodItemId: " + foodReservation.getFoodItemId());
            foodReservationRepo.save(foodReservation);
            foodReservationList.add(foodReservation);
        }
        //foodReservationRepo.saveAll(foodReservationList);
        return foodReservationList;
    }

    @Override
    public void cancelReservation(Integer orderId) {
        List<FoodReservation> foodReservationList = foodReservationRepo.findByOrderId(orderId);
        if (foodReservationList.isEmpty()) {
            throw new NotFoundException("Food Reservation list is not found by orderId: " + orderId);
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
}
