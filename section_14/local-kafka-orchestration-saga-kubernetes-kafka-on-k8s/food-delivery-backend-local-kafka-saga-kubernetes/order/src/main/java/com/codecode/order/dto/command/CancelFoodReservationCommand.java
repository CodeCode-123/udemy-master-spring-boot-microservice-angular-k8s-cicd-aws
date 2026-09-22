package com.codecode.order.dto.command;


import com.codecode.order.dto.FoodItemReservation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CancelFoodReservationCommand {
    private Integer orderId;
    private List<FoodItemReservation> foodItemReservationList;
}
