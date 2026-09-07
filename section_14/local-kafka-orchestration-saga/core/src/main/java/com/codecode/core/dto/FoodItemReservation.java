package com.codecode.core.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FoodItemReservation {
    private Integer orderId;
    private Integer foodItemId;
    private Double price;
    private Integer quantity;
}
