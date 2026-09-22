package com.codecode.foodcatalogue.repository;

import com.codecode.foodcatalogue.entity.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodItemRepo extends JpaRepository<FoodItem, Integer> {
//    @Query("SELECT f FROM FoodItem WHERE f.restaurantId = :restaurantId")
    List<FoodItem> findByRestaurantId(Integer restaurantId);
}
