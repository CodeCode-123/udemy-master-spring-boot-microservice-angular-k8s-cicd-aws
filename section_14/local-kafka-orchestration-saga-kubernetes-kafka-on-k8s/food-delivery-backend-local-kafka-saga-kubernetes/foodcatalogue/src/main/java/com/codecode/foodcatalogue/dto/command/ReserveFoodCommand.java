package com.codecode.foodcatalogue.dto.command;


import lombok.*;
import com.codecode.foodcatalogue.dto.FoodItemDTO;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReserveFoodCommand {
    private Integer orderId;
    private List<FoodItemDTO> foodItemDTOList;

    public String getFoodItemDTOListToString() {
        List<String> list = new ArrayList<>();
        for (FoodItemDTO foodItemDTO: foodItemDTOList) {
            String temp = foodItemDTO.toString();
            list.add(temp);
        }
        return String.join(";", list);
    }
}
