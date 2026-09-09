package com.codecode.core.dto;

import lombok.*;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FoodCataloguePage {
    private List<FoodItemDTO> foodItemList;
    private RestaurantDTO restaurantDTO;
}
