package com.codecode.order.repository;

import com.codecode.order.entity.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepo extends MongoRepository<Order, Integer> {
    Optional<Order> findByOrderId(Integer orderId);
}
