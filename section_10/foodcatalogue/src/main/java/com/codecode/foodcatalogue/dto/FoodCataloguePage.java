package com.codecode.foodcatalogue.dto;

import com.codecode.foodcatalogue.entity.FoodItem;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FoodCataloguePage {
    private List<FoodItemDTO> foodItemList;
    private RestaurantDTO restaurantDTO;
}
