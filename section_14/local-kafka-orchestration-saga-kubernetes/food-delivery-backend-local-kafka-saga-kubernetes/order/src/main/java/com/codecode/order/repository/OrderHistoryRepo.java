package com.codecode.order.repository;

import com.codecode.order.entity.OrderHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderHistoryRepo extends MongoRepository<OrderHistory, Integer> {
    Optional<OrderHistory> findByOrderHistoryId(Integer orderHistoryId);
    List<OrderHistory> findByOrderId(Integer orderId);
}
