package com.codecode.order.service;

import com.codecode.core.dto.OrderDTO;
import com.codecode.core.dto.UserDTO;
import com.codecode.core.dto.event.OrderCreatedEvent;
import com.codecode.core.types.OrderStatus;
import com.codecode.order.dto.*;
import com.codecode.order.entity.Order;
import com.codecode.order.repository.OrderRepo;
import jakarta.ws.rs.NotFoundException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class OrderService {
    private final OrderRepo orderRepo;
    private final SequenceGenerator sequenceGenerator;
    private final ReplyingKafkaTemplate<String, Object, Object> replyingKafkaTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String orderEventTopic;
    private final MongoTemplate mongoTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    @Value("${app.service.url}")
    private String url;

    @Value("${app.kafka.request-topic}")
    private String requestTopic;

    @Value("${app.kafka.reply-topic}")
    private String replyTopic;

    @Autowired
    public OrderService(OrderRepo orderRepo, SequenceGenerator sequenceGenerator,
                        ReplyingKafkaTemplate<String, Object, Object> replyingKafkaTemplate, KafkaTemplate<String, Object> kafkaTemplate,
                        @Value("${app.kafka.order-event-topic}") String orderEventTopic,
                        MongoTemplate mongoTemplate) {
        this.orderRepo = orderRepo;
        this.sequenceGenerator = sequenceGenerator;
        this.replyingKafkaTemplate = replyingKafkaTemplate;
        this.kafkaTemplate = kafkaTemplate;
        this.orderEventTopic = orderEventTopic;
        this.mongoTemplate = mongoTemplate;
    }

    public OrderDTO saveOrderInDb(OrderDTOFromFE orderDTOFromFE) throws ExecutionException, InterruptedException {
        Integer newOrderID = sequenceGenerator.generateNextOrderId();
        UserDTO userDTO = fetchUserDetailsFromUserId(orderDTOFromFE.getUserId());
        Order entity = new Order(newOrderID, orderDTOFromFE.getFoodItemsList(),
                orderDTOFromFE.getRestaurantDTO(), userDTO, OrderStatus.CREATED);
        //save to the mongodb
        orderRepo.save(entity);

        OrderCreatedEvent placedOrder = new OrderCreatedEvent(
                entity.getOrderId(),
                entity.getFoodItemsList(),
                entity.getRestaurantDTO(),
                entity.getUserDTO()
        );

        //send the orderCreatedEvent to the Kafka broker
        CompletableFuture<SendResult<String, Object>> orderCreatedFuture = kafkaTemplate.send(
                orderEventTopic, String.valueOf(placedOrder.getOrderId()), placedOrder);

        return new OrderDTO(
                entity.getOrderId(),
                entity.getFoodItemsList(),
                entity.getRestaurantDTO(),
                entity.getUserDTO()
        );
    }

    //Kafka request-reply pattern
    private UserDTO fetchUserDetailsFromUserId(Integer userId) throws ExecutionException, InterruptedException {
        ProducerRecord<String, Object> record = new ProducerRecord<>(requestTopic, userId);
        //Set reply topic header
        record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, replyTopic.getBytes()));
        RequestReplyFuture<String, Object, Object> sendAndReceive = replyingKafkaTemplate.sendAndReceive(record);
        ConsumerRecord<String, Object> response = sendAndReceive.get();
        return (UserDTO) response.value();
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

    public Order approveOrder(Integer orderId) {
        Query query = new Query(Criteria.where("orderId").is(orderId));
        Update update = new Update().set("orderStatus", OrderStatus.APPROVED);
        mongoTemplate.updateFirst(query, update, Order.class);
        Optional<Order> orderOptional = orderRepo.findByOrderId(orderId);
        if (orderOptional.isEmpty()) {
            throw new NotFoundException("Order is not found by orderId: " + orderId);
        }
        return orderOptional.get();
    }

    public Order rejectOrder(Integer orderId) {
        Query query = new Query(Criteria.where("orderId").is(orderId));
        Update update = new Update().set("orderStatus", OrderStatus.REJECTED);
        mongoTemplate.updateFirst(query, update, Order.class);
        Optional<Order> orderOptional = orderRepo.findByOrderId(orderId);
        if (orderOptional.isEmpty()) {
            throw new NotFoundException("Order is not found by orderId: " + orderId);
        }
        return orderOptional.get();
    }
}
