package com.codecode.message.service;

import com.codecode.message.dto.OrderDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Service
public class MessageService {
    private final ObjectMapper objectMapper;
    private final CacheManager cacheManager;
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageService.class);

    public MessageService(ObjectMapper objectMapper, CacheManager cacheManager) {
        this.objectMapper = objectMapper;
        this.cacheManager = cacheManager;
    }

    @KafkaListener(topics = "fetch-orderdto")
    //@Cacheable(value="orderdto", key="order")
    public OrderDTO getOrderDTO(String s) {
        LOGGER.info("Received orderDTO: m{}", s);
        OrderDTO orderDTO = objectMapper.readValue(s, OrderDTO.class);
        Cache cache = cacheManager.getCache("orderdto");
        if (cache != null) {
            cache.put(String.valueOf(orderDTO.getOrderId()), orderDTO);
            cache.put("order", orderDTO);
        }
        return orderDTO;
    }
}
