package com.codecode.core.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RestaurantDTO {
    private int id;
    private String name;
    private String address;
    private String city;
    private String restaurantDescription;

//    public RestaurantDTO(String name, String address, String city, String restaurantDescription) {
//        this.name = name;
//        this.address = address;
//        this.city = city;
//        this.restaurantDescription = restaurantDescription;
//    }
}
