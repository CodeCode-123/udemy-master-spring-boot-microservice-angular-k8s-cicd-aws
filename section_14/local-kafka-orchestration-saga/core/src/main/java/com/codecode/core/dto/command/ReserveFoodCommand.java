package com.codecode.core.dto.command;

import com.codecode.core.dto.FoodItemDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReserveFoodCommand {
    private Integer orderId;
    private List<FoodItemDTO> foodItemDTOList;
}
