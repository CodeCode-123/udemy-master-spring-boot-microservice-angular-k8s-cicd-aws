package com.codecode.message.dto.event;


import com.codecode.message.dto.FoodItemDTO;
import com.codecode.message.dto.UserDTO;
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
public class MessageRejectionEvent {
    private Integer orderId;
    private List<FoodItemDTO> foodItemDTOList;
    private UserDTO userDTO;

    @Override
    public String toString() {
        return "MessageRejectionEvent{" +
                "orderId=" + orderId +
                ", foodItemDTOList=" + foodItemDTOList +
                ", userDTO=" + userDTO +
                '}';
    }

    private String getFoodItemDTOListToString() {
        List<String> list = new ArrayList<>();
        for (FoodItemDTO foodItemDTO: foodItemDTOList) {
            String temp = foodItemDTO.toString();
            list.add(temp);
        }
        return String.join(";", list);
    }
}
