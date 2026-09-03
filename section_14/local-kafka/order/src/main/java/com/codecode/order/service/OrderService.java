package com.codecode.order.service;

import com.codecode.order.dto.OrderDTO;
import com.codecode.order.dto.OrderDTOFromFE;
import com.codecode.order.dto.UserDTO;
import com.codecode.order.entity.Order;
import com.codecode.order.repository.OrderRepo;
import jakarta.ws.rs.NotFoundException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
public class OrderService {
    private final OrderRepo orderRepo;
    private final SequenceGenerator sequenceGenerator;
    private final ReplyingKafkaTemplate<String, String, String> replyingKafkaTemplate;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${app.service.url}")
    private String url;

    @Value("${app.kafka.request-topic}")
    private String requestTopic;

    @Value("${app.kafka.reply-topic}")
    private String replyTopic;

    @Autowired
    public OrderService(OrderRepo orderRepo, SequenceGenerator sequenceGenerator,
                        ReplyingKafkaTemplate<String, String, String> replyingKafkaTemplate,
                        ObjectMapper objectMapper,
                        KafkaTemplate<String, String> kafkaTemplate) {
        this.orderRepo = orderRepo;
        this.sequenceGenerator = sequenceGenerator;
        this.replyingKafkaTemplate = replyingKafkaTemplate;
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    public OrderDTO saveOrderInDb(OrderDTOFromFE orderDTOFromFE) throws ExecutionException, InterruptedException {
        Integer newOrderID = sequenceGenerator.generateNextOrderId();
        UserDTO userDTO = fetchUserDetailsFromUserId(orderDTOFromFE.getUserId());
        Order orderToBeSaved = new Order(newOrderID, orderDTOFromFE.getFoodItemsList(),
                orderDTOFromFE.getRestaurantDTO(), userDTO);
        orderRepo.save(orderToBeSaved);
        OrderDTO orderDTO = mapOrderToOrderDTO(orderToBeSaved);
        //send to kafka broker
        kafkaTemplate.send("fetch-orderdto", String.valueOf(orderDTO.getOrderId()), objectMapper.writeValueAsString(orderDTO));
        return orderDTO;
    }

    //Kafka request-reply pattern
    private UserDTO fetchUserDetailsFromUserId(Integer userId) throws ExecutionException, InterruptedException {
        ProducerRecord<String, String> record = new ProducerRecord<>(requestTopic, String.valueOf(userId));
        //Set reply topic header
        record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, replyTopic.getBytes()));
        RequestReplyFuture<String, String, String> sendAndReceive = replyingKafkaTemplate.sendAndReceive(record);
        ConsumerRecord<String, String> response = sendAndReceive.get();
        return objectMapper.readValue(response.value(), UserDTO.class);
    }

    public List<OrderDTO> getAllOrders() {
        List<Order> orders = orderRepo.findAll();
        List<OrderDTO> orderDTOs = new ArrayList<>();
        for (Order order: orders) {
            OrderDTO temp = mapOrderToOrderDTO(order);
            orderDTOs.add(temp);
        }
        return orderDTOs;
    }

    public OrderDTO getOrderByOrderId(Integer orderId) {
        Optional<Order> orderOptional = orderRepo.findByOrderId(orderId);
        if (orderOptional.isEmpty()) {
            throw new NotFoundException("Order is not Found by orderId: " + orderId);
        }
        Order order = orderOptional.get();
        return mapOrderToOrderDTO(order);
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
