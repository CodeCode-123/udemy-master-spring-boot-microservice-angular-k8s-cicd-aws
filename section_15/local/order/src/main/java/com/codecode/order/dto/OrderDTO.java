package com.codecode.order.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderDTO {
    private Integer orderId;
    private List<FoodItemDTO> foodItemsList;
    private RestaurantDTO restaurantDTO;
    private UserDTO userDTO;
}
