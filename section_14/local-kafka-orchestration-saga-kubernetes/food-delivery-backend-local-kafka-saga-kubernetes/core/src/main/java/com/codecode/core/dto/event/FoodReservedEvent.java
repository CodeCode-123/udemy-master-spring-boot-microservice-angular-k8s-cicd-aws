package com.codecode.core.dto.event;

import com.codecode.core.dto.FoodItemReservation;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FoodReservedEvent {
    List<FoodItemReservation> foodItemReservationList;

    public String getFoodReservedEventToString() {
        List<String> list = new ArrayList<>();
        for (FoodItemReservation foodItemReservation: foodItemReservationList) {
            String temp = foodItemReservation.toString();
            list.add(temp);
        }
        return String.join(";", list);
    }
}


