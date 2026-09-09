package com.codecode.order.dto;

import com.codecode.core.dto.FoodItemDTO;
import com.codecode.core.dto.RestaurantDTO;
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
