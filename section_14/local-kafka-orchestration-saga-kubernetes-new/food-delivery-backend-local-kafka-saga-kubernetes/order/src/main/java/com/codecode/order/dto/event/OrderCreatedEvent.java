package com.codecode.order.dto.event;


import com.codecode.order.dto.FoodItemDTO;
import com.codecode.order.dto.RestaurantDTO;
import com.codecode.order.dto.UserDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderCreatedEvent {
    private Integer orderId;
    private List<FoodItemDTO> foodItemsList;
    private RestaurantDTO restaurantDTO;
    private UserDTO userDTO;
}
