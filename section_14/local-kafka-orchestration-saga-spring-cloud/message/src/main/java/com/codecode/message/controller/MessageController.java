package com.codecode.message.controller;

import com.codecode.core.dto.event.MessageApprovalEvent;
import com.codecode.core.dto.event.MessageRejectionEvent;
import com.codecode.message.dto.MessageContactDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/message")
public class MessageController {
    private final CacheManager cacheManager;
    private final MessageContactDTO messageContactDTO;

    @Value("${build.version}")
    private String buildVersion;

    public MessageController(CacheManager cacheManager, MessageContactDTO messageContactDTO) {
        this.cacheManager = cacheManager;
        this.messageContactDTO = messageContactDTO;
    }

    @GetMapping("/orderapproval")
    public MessageApprovalEvent getMessageApprovalEvent() {
        Cache cache = cacheManager.getCache("order-approval");
        if (cache != null) {
            return cache.get("approval", MessageApprovalEvent.class);
        }
        return null;
    }

    @GetMapping("/orderapproval/{orderId}")
    public MessageApprovalEvent getMessageApprovalEventByOrderId(@PathVariable("orderId") Integer orderId) {
        Cache cache = cacheManager.getCache("order-approval");
        if (cache != null) {
            return cache.get(String.valueOf(orderId), MessageApprovalEvent.class);
        }
        return null;
    }

    @GetMapping("/orderrejection")
    public MessageRejectionEvent getMessageRejectionEvent() {
        Cache cache = cacheManager.getCache("order-rejection");
        if (cache != null) {
            return cache.get("rejection", MessageRejectionEvent.class);
        }
        return null;
    }

    @GetMapping("/orderrejection/{orderId}")
    public MessageRejectionEvent getMessageRejectionEventByOrderId(@PathVariable("orderId") Integer orderId) {
        Cache cache = cacheManager.getCache("order-rejection");
        if (cache != null) {
            return cache.get(String.valueOf(orderId), MessageRejectionEvent.class);
        }
        return null;
    }

    @GetMapping("/build-version")
    public ResponseEntity<String> getBuildVersion() {
        return new ResponseEntity<>(buildVersion, HttpStatus.OK);
    }

    @GetMapping("/contact-info")
    public ResponseEntity<MessageContactDTO> getContactInfo() {
        return new ResponseEntity<>(messageContactDTO, HttpStatus.OK);
    }

}
