package com.codecode.order.entity;


import com.codecode.order.dto.FoodItemDTO;
import com.codecode.order.dto.RestaurantDTO;
import com.codecode.order.dto.UserDTO;
import com.codecode.order.dto.types.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document("ordersaga")
public class Order {
    @Indexed
    private Integer orderId;
    private List<FoodItemDTO> foodItemsList;
    private RestaurantDTO restaurantDTO;
    private UserDTO userDTO;
    private OrderStatus orderStatus;
}
