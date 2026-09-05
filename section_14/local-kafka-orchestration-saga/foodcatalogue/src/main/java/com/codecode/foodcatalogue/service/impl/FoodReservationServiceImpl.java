package com.codecode.foodcatalogue.service.impl;

import com.codecode.core.dto.FoodItemDTO;
import com.codecode.foodcatalogue.service.FoodReservationService;
import org.springframework.stereotype.Service;

@Service
public class FoodReservationServiceImpl implements FoodReservationService {
    @Override
    public FoodItemDTO reserve(FoodItemDTO foodItemDTO, Integer orderId) {
        return null;
    }

    @Override
    public void cancelReservation(FoodItemDTO foodItemToCancel, Integer orderId) {

    }
}
