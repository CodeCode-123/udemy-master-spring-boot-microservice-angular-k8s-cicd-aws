package com.codecode.foodcatalogue.entity;

import jakarta.persistence.Column;

public class FoodReservation {
    private int id;
    private Integer foodItemId;
    private String itemName;
    private boolean isVeg;
    private Double price;
    private Integer restaurantId;
    private Integer quantity;
    private Integer orderId;
}
