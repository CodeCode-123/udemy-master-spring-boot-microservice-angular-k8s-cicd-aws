package com.codecode.core.dto.event;

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
public class PaymentFailedEvent {
    private Integer orderId;
    private Double totalPrice;
    private List<FoodItemReservation> foodItemReservationList;

    @Override
    public String toString() {
        return "PaymentFailedEvent{" +
                "orderId=" + orderId +
                ", totalPrice=" + totalPrice +
                ", foodItemReservationList=" + getFoodItemReservationListToString() +
                '}';
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
