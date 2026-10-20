package com.codecode.foodcatalogue.dto;

import lombok.Data;

@Data
public class RestaurantDTO {
    private int id;
    private String name;
    private String address;
    private String city;
    private String restaurantDescription;
}
