package com.codecode.order.controller;

import com.codecode.order.dto.OrderDTO;
import com.codecode.order.dto.OrderDTOFromFE;
import com.codecode.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/saveOrder")
    public ResponseEntity<OrderDTO> saveOrder(@RequestBody OrderDTOFromFE orderDTOFromFE) {
        OrderDTO orderSavedInDB = orderService.saveOrderInDb(orderDTOFromFE);
        return new ResponseEntity<>(orderSavedInDB, HttpStatus.OK);
    }
}
