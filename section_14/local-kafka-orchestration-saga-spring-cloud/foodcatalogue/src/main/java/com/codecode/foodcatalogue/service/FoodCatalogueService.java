package com.codecode.foodcatalogue.service;

import com.codecode.core.dto.FoodCataloguePage;
import com.codecode.core.dto.FoodItemDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface FoodCatalogueService {
    FoodItemDTO addFoodItem(FoodItemDTO foodItemDTO);
    ResponseEntity<FoodCataloguePage> fetchFoodCataloguePageDetails(Integer restaurantId) throws Exception;
    List<FoodItemDTO> fetchFoodItemList(Integer restaurantId);
}
