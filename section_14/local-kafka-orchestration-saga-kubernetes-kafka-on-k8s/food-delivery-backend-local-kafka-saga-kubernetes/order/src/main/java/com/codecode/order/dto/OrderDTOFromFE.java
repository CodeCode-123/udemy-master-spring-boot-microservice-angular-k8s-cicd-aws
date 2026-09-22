package com.codecode.order.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderDTOFromFE {
    private List<FoodItemDTO> foodItemsList;
    private Integer userId;
    private RestaurantDTO restaurantDTO;
}
