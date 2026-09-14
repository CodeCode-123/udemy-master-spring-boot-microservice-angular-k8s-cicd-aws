package com.codecode.order.controller;


import com.codecode.core.dto.OrderDTO;
import com.codecode.order.dto.OrderDTOFromFE;
import com.codecode.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/order")
@CrossOrigin
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<OrderDTO> orderDTOs = orderService.getAllOrders();
        return new ResponseEntity<>(orderDTOs, HttpStatus.OK);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getByOrderId(@PathVariable("orderId") Integer orderId) {
        OrderDTO orderDTO = orderService.getOrderByOrderId(orderId);
        return new ResponseEntity<>(orderDTO, HttpStatus.OK);
    }

    @PostMapping("/saveOrder")
    public ResponseEntity<OrderDTO> saveOrder(@RequestBody OrderDTOFromFE orderDTOFromFE) throws ExecutionException, InterruptedException {
        OrderDTO orderSavedInDB = orderService.saveOrderInDb(orderDTOFromFE);
        return new ResponseEntity<>(orderSavedInDB, HttpStatus.OK);
    }
}
