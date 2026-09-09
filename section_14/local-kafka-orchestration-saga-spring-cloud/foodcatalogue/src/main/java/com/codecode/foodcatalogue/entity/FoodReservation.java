package com.codecode.foodcatalogue.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="food_reservation", indexes = {@Index(name="idx_order_id", columnList = "order_id")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FoodReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private Integer foodItemId;
    private String itemName;
    private boolean isVeg;
    private Double price;
    private Integer restaurantId;
    private Integer quantity;
    @Column(name="order_id")
    private Integer orderId;

    public FoodReservation(Integer foodItemId, String itemName, boolean isVeg, Double price, Integer restaurantId, Integer quantity, Integer orderId) {
        this.foodItemId = foodItemId;
        this.itemName = itemName;
        this.isVeg = isVeg;
        this.price = price;
        this.restaurantId = restaurantId;
        this.quantity = quantity;
        this.orderId = orderId;
    }
}
