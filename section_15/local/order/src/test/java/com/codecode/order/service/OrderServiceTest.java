package com.codecode.order.service;

import com.codecode.order.dto.*;
import com.codecode.order.entity.Order;
import com.codecode.order.repository.OrderRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    @Mock
    private OrderRepo orderRepo;

    @Mock
    private SequenceGenerator sequenceGenerator;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createOrder_shouldGenerateOrder() {

        Integer orderId = 1;
        List<FoodItemDTO> foodItemDTOList = List.of(new FoodItemDTO());
        RestaurantDTO restaurantDTO = new RestaurantDTO();
        UserDTO userDTO = new UserDTO();
        Order order1 = new Order();
        order1.setOrderId(orderId);
        order1.setFoodItemsList(foodItemDTOList);
        order1.setRestaurantDTO(restaurantDTO);
        order1.setUserDTO(userDTO);

        Order order2 = new Order(foodItemDTOList, restaurantDTO, userDTO);
        order2.setOrderId(orderId);
        assertEquals(order1, order2);
    }

    @Test
    void saveOrderInDb_shouldSaveOrderAndReturnOrderDTO() {
        //Arrange
        OrderDTOFromFE orderDTOFromFE = new OrderDTOFromFE();
        Integer newOrderId = 1;
        UserDTO userDTO = new UserDTO();
        Order orderToBeSaved = new Order(newOrderId, orderDTOFromFE.getFoodItemsList(), orderDTOFromFE.getRestaurantDTO(), userDTO);
        OrderDTO orderDTOExpected = orderService.mapOrderToOrderDTO(orderToBeSaved);

        when(sequenceGenerator.generateNextOrderId()).thenReturn(newOrderId);
        when(restTemplate.getForObject(anyString(), eq(UserDTO.class))).thenReturn(userDTO);
        when(orderRepo.save(orderToBeSaved)).thenReturn(orderToBeSaved);

        //Act
        OrderDTO orderDTOActual = orderService.saveOrderInDb(orderDTOFromFE);

        //Assert
        verify(sequenceGenerator, times(1)).generateNextOrderId();
        assertDoesNotThrow(() -> orderService.saveOrderInDb(orderDTOFromFE));
    }
}
