package com.codecode.core.dto.command;

import com.codecode.core.dto.FoodItemDTO;
import lombok.*;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReserveFoodCommand {
    private Integer orderId;
    private List<FoodItemDTO> foodItemDTOList;
}
