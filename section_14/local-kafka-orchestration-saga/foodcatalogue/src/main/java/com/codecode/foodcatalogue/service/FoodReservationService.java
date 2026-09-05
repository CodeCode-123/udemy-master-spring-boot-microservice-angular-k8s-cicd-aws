package com.codecode.foodcatalogue.service;


import com.codecode.core.dto.FoodItemDTO;

public interface FoodReservationService {
    FoodItemDTO reserve(FoodItemDTO foodItemDTO, Integer orderId);
    void cancelReservation(FoodItemDTO foodItemToCancel, Integer orderId);
}
