package com.codecode.core.dto.event;

import com.codecode.core.dto.FoodItemDTO;
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
public class MessageApprovalEvent {
    private Integer orderId;
    private List<FoodItemDTO> foodItemDTOList;
    private UserDTO userDTO;

    @Override
    public String toString() {
        return "MessageApprovalEvent{" +
                "orderId=" + orderId +
                ", foodItemDTOList=" + getFoodItemDTOListToString() +
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
