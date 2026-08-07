package com.codecode.order.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FoodItemDTO {
    private int id;
    private String itemName;
    private String itemDescription;
    private boolean isVeg;
    private Number price;
    private Integer restaurantId;
    private Integer quantity;
}
