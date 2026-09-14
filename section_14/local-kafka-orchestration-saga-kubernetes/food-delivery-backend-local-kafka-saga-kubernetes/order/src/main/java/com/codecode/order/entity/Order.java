package com.codecode.order.entity;

import com.codecode.core.dto.FoodItemDTO;
import com.codecode.core.dto.RestaurantDTO;
import com.codecode.core.dto.UserDTO;
import com.codecode.core.types.OrderStatus;
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
