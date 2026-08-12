package com.codecode.order.service;

import com.codecode.order.dto.OrderDTO;
import com.codecode.order.dto.OrderDTOFromFE;
import com.codecode.order.dto.UserDTO;
import com.codecode.order.entity.Order;
import com.codecode.order.repository.OrderRepo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OrderService {
    private final OrderRepo orderRepo;
    private final SequenceGenerator sequenceGenerator;
    private final RestTemplate restTemplate;
    @Value("${app.service.url}")
    private String url;

    @Autowired
    public OrderService(OrderRepo orderRepo, SequenceGenerator sequenceGenerator,
                        RestTemplate restTemplate) {
        this.orderRepo = orderRepo;
        this.sequenceGenerator = sequenceGenerator;
        this.restTemplate = restTemplate;
    }

    public OrderDTO saveOrderInDb(OrderDTOFromFE orderDTOFromFE) {
        Integer newOrderID = sequenceGenerator.generateNextOrderId();
        UserDTO userDTO = fetchUserDetailsFromUserId(orderDTOFromFE.getUserId());
        Order orderToBeSaved = new Order(newOrderID, orderDTOFromFE.getFoodItemsList(),
                orderDTOFromFE.getRestaurantDTO(), userDTO);
        orderRepo.save(orderToBeSaved);
        return mapOrderToOrderDTO(orderToBeSaved);
    }

    private UserDTO fetchUserDetailsFromUserId(Integer userId) {
        return restTemplate.getForObject(url + userId, UserDTO.class);
    }

    private OrderDTO mapOrderToOrderDTO(Order order) {
        OrderDTO orderDTO = new OrderDTO();
        BeanUtils.copyProperties(order, orderDTO);
        return orderDTO;
    }

    private Order mapOrderDTOToOrder(OrderDTO orderDTO) {
        Order order = new Order();
        BeanUtils.copyProperties(orderDTO, order);
        return order;
    }
}
