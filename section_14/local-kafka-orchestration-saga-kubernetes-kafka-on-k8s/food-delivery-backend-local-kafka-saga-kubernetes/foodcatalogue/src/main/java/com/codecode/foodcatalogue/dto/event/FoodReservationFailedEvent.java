package com.codecode.foodcatalogue.dto.event;

import com.codecode.foodcatalogue.dto.FoodItemReservation;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FoodReservationFailedEvent {
    List<FoodItemReservation> foodItemReservationList;

    public String getFoodReservationFailedEventToString() {
        List<String> list = new ArrayList<>();
        for (FoodItemReservation foodItemReservation: foodItemReservationList) {
            String temp = foodItemReservation.toString();
            list.add(temp);
        }
        return String.join(";", list);
    }
}
