package com.codecode.foodcatalogue.service;


import com.codecode.core.dto.FoodItemDTO;
import com.codecode.core.dto.FoodItemReservation;
import com.codecode.foodcatalogue.entity.FoodReservation;

import java.util.List;

public interface FoodReservationService {
    List<FoodReservation> reserve(List<FoodItemDTO> foodItemDTOList, Integer orderId);
    void cancelReservation(Integer orderId);
    void cancelReservation(Integer orderId, List<FoodItemReservation> foodItemReservationList);
}
