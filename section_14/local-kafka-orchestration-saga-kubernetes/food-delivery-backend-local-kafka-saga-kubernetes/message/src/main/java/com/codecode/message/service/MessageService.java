package com.codecode.message.service;

import com.codecode.core.dto.command.OrderApprovalMessageCommand;
import com.codecode.core.dto.command.OrderRejectionMessageCommand;
import com.codecode.core.dto.event.MessageApprovalEvent;
import com.codecode.core.dto.event.MessageRejectionEvent;
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
    private final CacheManager cacheManager;
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageService.class);

    public MessageService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public MessageApprovalEvent getMessageApprovalEvent(OrderApprovalMessageCommand command) {
        MessageApprovalEvent event = new MessageApprovalEvent();
        event.setOrderId(command.getOrderId());
        event.setFoodItemDTOList(command.getFoodItemDTOList());
        event.setUserDTO(command.getUserDTO());
        Cache cache = cacheManager.getCache("order-approval");
        if (cache != null) {
            cache.put(String.valueOf(event.getOrderId()), event);
            cache.put("approval", event);
        }
        return event;
    }

    public MessageRejectionEvent getMessageRejectionEvent(OrderRejectionMessageCommand command) {
        MessageRejectionEvent event = new MessageRejectionEvent();
        event.setOrderId(command.getOrderId());
        event.setFoodItemDTOList(command.getFoodItemDTOList());
        event.setUserDTO(command.getUserDTO());
        Cache cache = cacheManager.getCache("order-rejection");
        if (cache != null) {
            cache.put(String.valueOf(event.getOrderId()), event);
            cache.put("rejection", event);
        }
        return event;
    }
}
