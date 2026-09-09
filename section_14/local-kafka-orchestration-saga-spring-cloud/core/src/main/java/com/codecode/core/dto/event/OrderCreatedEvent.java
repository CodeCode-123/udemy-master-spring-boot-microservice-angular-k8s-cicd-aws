package com.codecode.core.dto.event;

import com.codecode.core.dto.FoodItemDTO;
import com.codecode.core.dto.RestaurantDTO;
import com.codecode.core.dto.UserDTO;
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
