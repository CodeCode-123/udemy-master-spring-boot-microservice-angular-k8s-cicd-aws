package com.codecode.core.dto.command;

import com.codecode.core.dto.FoodItemDTO;
import com.codecode.core.dto.OrderDTO;
import com.codecode.core.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRejectionMessageCommand {
    private Integer orderId;
    private List<FoodItemDTO> foodItemDTOList;
    private UserDTO userDTO;

    private String getFoodItemDTOListToString() {
        List<String> list = new ArrayList<>();
        for (FoodItemDTO foodItemDTO: foodItemDTOList) {
            String temp = foodItemDTO.toString();
            list.add(temp);
        }
        return String.join(";", list);
    }
}
