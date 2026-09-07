package com.codecode.foodcatalogue.repository;

import com.codecode.foodcatalogue.entity.FoodReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodReservationRepo extends JpaRepository<FoodReservation, Integer> {
    List<FoodReservation> findByOrderId(Integer orderId);
    void deleteByOrderId(Integer orderId);
}
