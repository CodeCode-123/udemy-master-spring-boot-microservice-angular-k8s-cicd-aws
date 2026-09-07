package com.codecode.core.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FoodItemDTO {
    private int id;
    private String itemName;
    private String itemDescription;
    private Boolean isVeg;
    private Double price;
    private Integer restaurantId;
    private Integer quantity;
}
