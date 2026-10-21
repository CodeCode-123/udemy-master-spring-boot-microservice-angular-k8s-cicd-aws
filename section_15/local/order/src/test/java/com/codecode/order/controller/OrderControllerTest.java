package com.codecode.order.controller;

import com.codecode.order.dto.OrderDTO;
import com.codecode.order.dto.OrderDTOFromFE;
import com.codecode.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveOrder_ShouldReturnCreatedStatus() {
        //Arrange
        OrderDTOFromFE orderDTOFromFE = new OrderDTOFromFE();
        OrderDTO orderSavedInDB = new OrderDTO();
        when(orderService.saveOrderInDb(orderDTOFromFE)).thenReturn(orderSavedInDB);

        //Act
        ResponseEntity<OrderDTO> response = orderController.saveOrder(orderDTOFromFE);

        //Assert
        verify(orderService, times(1)).saveOrderInDb(orderDTOFromFE);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(orderSavedInDB, response.getBody());
    }
}
