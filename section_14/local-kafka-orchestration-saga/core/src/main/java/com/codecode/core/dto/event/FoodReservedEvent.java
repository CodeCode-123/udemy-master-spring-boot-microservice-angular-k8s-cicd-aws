package com.codecode.core.dto.event;

import com.codecode.core.dto.FoodItemReservation;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FoodReservedEvent {
    List<FoodItemReservation> foodItemReservationList;
}


