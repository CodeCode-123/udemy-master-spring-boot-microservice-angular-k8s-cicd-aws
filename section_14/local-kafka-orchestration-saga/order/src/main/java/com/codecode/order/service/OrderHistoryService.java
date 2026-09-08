package com.codecode.order.service;

import com.codecode.core.dto.event.OrderCreatedEvent;
import com.codecode.core.types.OrderStatus;
import com.codecode.order.entity.OrderHistory;
import com.codecode.order.repository.OrderHistoryRepo;
import jakarta.ws.rs.NotFoundException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;

@Service
public class OrderHistoryService {

    private final OrderHistoryRepo orderHistoryRepo;
    private final HistorySequenceGenerator historySequenceGenerator;

    public OrderHistoryService(OrderHistoryRepo orderHistoryRepo, HistorySequenceGenerator historySequenceGenerator) {
        this.orderHistoryRepo = orderHistoryRepo;
        this.historySequenceGenerator = historySequenceGenerator;
    }

    public OrderHistory saveOrderHistoryInDb(OrderCreatedEvent orderCreatedEvent) {
        Integer newOrderHistoryID = historySequenceGenerator.generateNextOrderId();
        OrderHistory orderHistory = new OrderHistory();
        orderHistory.setOrderHistoryId(newOrderHistoryID);
        orderHistory.setOrderId(orderCreatedEvent.getOrderId());
        orderHistory.setOrderStatus(OrderStatus.CREATED);
        orderHistory.setCreatedAt(new Timestamp(new Date().getTime()));
        return orderHistoryRepo.save(orderHistory);
    }

    public OrderHistory getOrderHistoryById(Integer orderHistoryId) {
        Optional<OrderHistory> orderHistoryOptional = orderHistoryRepo.findByOrderHistoryId(orderHistoryId);
        if (orderHistoryOptional.isEmpty()) {
            throw new NotFoundException("Order History is not found by orderHistoryId: " + orderHistoryId);
        }
        return orderHistoryOptional.get();
    }

    public OrderHistory add(Integer orderId, OrderStatus orderStatus) {
        Integer newOrderHistoryID = historySequenceGenerator.generateNextOrderId();
        OrderHistory orderHistory = new OrderHistory();
        orderHistory.setOrderHistoryId(newOrderHistoryID);
        orderHistory.setOrderId(orderId);
        orderHistory.setOrderStatus(orderStatus);
        orderHistory.setCreatedAt(new Timestamp(new Date().getTime()));
        return orderHistoryRepo.save(orderHistory);
    }
}
