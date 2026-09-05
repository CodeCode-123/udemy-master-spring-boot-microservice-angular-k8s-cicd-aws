package com.codecode.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FoodCataloguePage {
    private List<FoodItemDTO> foodItemList;
    private RestaurantDTO restaurantDTO;
}
