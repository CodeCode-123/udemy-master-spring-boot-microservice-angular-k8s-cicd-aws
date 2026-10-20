package com.codecode.foodcatalogue.dto;

import lombok.Data;


import java.util.List;

@Data
public class FoodCataloguePage {
    private List<FoodItemDTO> foodItemList;
    private RestaurantDTO restaurantDTO;
}
