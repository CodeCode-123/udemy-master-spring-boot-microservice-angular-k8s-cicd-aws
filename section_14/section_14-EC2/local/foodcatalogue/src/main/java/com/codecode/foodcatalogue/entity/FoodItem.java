package com.codecode.foodcatalogue.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class FoodItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String itemName;
    private String itemDescription;
    private boolean isVeg;
    private Double price;
    private Integer restaurantId;
    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer quantity;

    public FoodItem() {
    }

    public FoodItem(String itemName, String itemDescription, boolean isVeg, Double price, Integer restaurantId, Integer quantity) {
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.isVeg = isVeg;
        this.price = price;
        this.restaurantId = restaurantId;
        this.quantity = quantity;
    }
}
