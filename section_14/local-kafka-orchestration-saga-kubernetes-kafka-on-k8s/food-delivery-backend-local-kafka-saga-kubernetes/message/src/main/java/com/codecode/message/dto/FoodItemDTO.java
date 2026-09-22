package com.codecode.message.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FoodItemDTO {
    private int id;
    private String itemName;
    private String itemDescription;
    private Boolean isVeg;
    private Double price;
    private Integer restaurantId;
    private Integer quantity;

    @Override
    public String toString() {
        return "FoodItemDTO{" +
                "id=" + id +
                ", itemName='" + itemName + '\'' +
                ", itemDescription='" + itemDescription + '\'' +
                ", isVeg=" + isVeg +
                ", price=" + price +
                ", restaurantId=" + restaurantId +
                ", quantity=" + quantity +
                '}';
    }
}
