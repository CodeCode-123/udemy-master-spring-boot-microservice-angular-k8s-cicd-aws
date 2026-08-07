package com.codecode.order.entity;

import com.codecode.order.dto.FoodItemDTO;
import com.codecode.order.dto.RestaurantDTO;
import com.codecode.order.dto.UserDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@Document("order")
public class Order {
    private Integer orderId;
    private List<FoodItemDTO> foodItemsList;
    private RestaurantDTO restaurantDTO;
    private UserDTO userDTO;

    public Order() {
    }

    public Order(List<FoodItemDTO> foodItemsList, RestaurantDTO restaurantDTO, UserDTO userDTO) {
        this.foodItemsList = foodItemsList;
        this.restaurantDTO = restaurantDTO;
        this.userDTO = userDTO;
    }

    public Order(Integer orderId, List<FoodItemDTO> foodItemsList, RestaurantDTO restaurantDTO, UserDTO userDTO) {
        this.orderId = orderId;
        this.foodItemsList = foodItemsList;
        this.restaurantDTO = restaurantDTO;
        this.userDTO = userDTO;
    }
}
