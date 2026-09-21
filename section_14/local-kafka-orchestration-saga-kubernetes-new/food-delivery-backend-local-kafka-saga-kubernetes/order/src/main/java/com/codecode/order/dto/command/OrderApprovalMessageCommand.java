package com.codecode.order.dto.command;


import com.codecode.order.dto.FoodItemDTO;
import com.codecode.order.dto.UserDTO;
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
public class OrderApprovalMessageCommand {
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
