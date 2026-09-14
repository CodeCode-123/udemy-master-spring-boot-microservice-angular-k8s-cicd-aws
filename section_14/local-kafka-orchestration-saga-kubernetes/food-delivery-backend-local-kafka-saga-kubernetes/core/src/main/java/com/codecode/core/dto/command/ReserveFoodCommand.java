package com.codecode.core.dto.command;


import com.codecode.core.dto.FoodItemDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

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
