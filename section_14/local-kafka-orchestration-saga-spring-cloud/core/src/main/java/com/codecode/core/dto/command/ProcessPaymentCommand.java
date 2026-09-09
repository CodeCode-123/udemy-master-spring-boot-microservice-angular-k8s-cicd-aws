package com.codecode.core.dto.command;

import com.codecode.core.dto.FoodItemReservation;
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
public class ProcessPaymentCommand {
    private Integer orderId;
    private List<FoodItemReservation> foodItemReservationList;
    private Double totalPrice;

    public String getProcessPaymentCommandToString() {
        return "orderId: " + orderId
                + ", foodItemReservationList: " + getFoodItemReservationListToString()
                + ", totalPrice: " + totalPrice;
    }

    private String getFoodItemReservationListToString() {
        List<String> list = new ArrayList<>();
        for (FoodItemReservation foodItemReservation: foodItemReservationList) {
            String temp = foodItemReservation.toString();
            list.add(temp);
        }
        return String.join(";", list);
    }
}
