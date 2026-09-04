package com.codecode.order.service;

import com.codecode.order.dto.*;
import com.codecode.order.entity.Order;
import com.codecode.order.exception.MessageKafkaException;
import com.codecode.order.exception.PaymentKafkaException;
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
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class OrderService {
    private final OrderRepo orderRepo;
    private final SequenceGenerator sequenceGenerator;
    private final ReplyingKafkaTemplate<String, String, String> replyingKafkaTemplate;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    @Value("${app.service.url}")
    private String url;

    @Value("${app.kafka.request-topic}")
    private String requestTopic;

    @Value("${app.kafka.reply-topic}")
    private String replyTopic;

    @Value("${app.kafka.fetch-orderdto-topic}")
    private String fetchOrderDTOTopic;

    @Value("${app.kafka.fetch-orderpaymentdto-topic}")
    private String fetchOrderPaymentDTOTopic;

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

    //don't rollback if it is the message service exception, rollback for all the other exceptions, include PaymentServiceException
    @Transactional(value="kafkaTransactionManager", noRollbackFor = {MessageKafkaException.class})
    public OrderDTO saveOrderInDb(OrderDTOFromFE orderDTOFromFE) throws ExecutionException, InterruptedException {
        Integer newOrderID = sequenceGenerator.generateNextOrderId();
        UserDTO userDTO = fetchUserDetailsFromUserId(orderDTOFromFE.getUserId());
        Order orderToBeSaved = new Order(newOrderID, orderDTOFromFE.getFoodItemsList(),
                orderDTOFromFE.getRestaurantDTO(), userDTO);
        orderRepo.save(orderToBeSaved);
        OrderDTO orderDTO = mapOrderToOrderDTO(orderToBeSaved);

        //calculate the total amount and create an orderPaymentDTO
        double amount = calculateTotalAmount(orderDTO);
        OrderPaymentDTO orderPaymentDTO = new OrderPaymentDTO(String.valueOf(orderDTO.getOrderId()), amount);
        //send to kafka broker
        CompletableFuture<SendResult<String, String>> paymentFuture = kafkaTemplate.send(fetchOrderPaymentDTOTopic,
                String.valueOf(orderPaymentDTO.getOrderId()), objectMapper.writeValueAsString(orderPaymentDTO));
        paymentFuture.exceptionally(ex -> {
            throw new PaymentKafkaException("Failed to send to Payment Service: " + ex.getMessage());
        });

        // Added method to test @Transactional
        //throwPaymentException();
        //throwMessageException();

        //send to kafka broker
        CompletableFuture<SendResult<String, String>> messageFuture = kafkaTemplate.send(fetchOrderDTOTopic,
                String.valueOf(orderDTO.getOrderId()), objectMapper.writeValueAsString(orderDTO));
        messageFuture.exceptionally(ex -> {
            throw new MessageKafkaException("Failed to send to Message Service: " + ex.getMessage());
        });

        // Added method to test @Transactional
        //throwMessageException();

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

    private double calculateTotalAmount(OrderDTO orderDTO) {
        List<FoodItemDTO> foodItemDTOList = orderDTO.getFoodItemsList();
        double sum = 0;
        for (FoodItemDTO foodItemDTO: foodItemDTOList) {
            double price = foodItemDTO.getPrice();
            int quantity = foodItemDTO.getQuantity();
            sum += price * quantity;
        }
        return sum;
    }

    private void throwPaymentException() throws PaymentKafkaException {
        throw new PaymentKafkaException("Failed to send Payment");
    }

    private void throwMessageException() throws MessageKafkaException {
        throw new MessageKafkaException("Failed to send Message");
    }
}
