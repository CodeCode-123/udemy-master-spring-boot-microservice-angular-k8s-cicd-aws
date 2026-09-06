package com.codecode.message.controller;

import jakarta.ws.rs.Path;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/message")
public class MessageController {
    private final CacheManager cacheManager;

    public MessageController(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

//    @GetMapping("/order")
//    public OrderDTO getOrderDTO() {
//        Cache cache = cacheManager.getCache("orderdto");
//        if (cache != null) {
//            return cache.get("order", OrderDTO.class);
//        }
//        return null;
//    }
//
//    @GetMapping("/order/{orderId}")
//    public OrderDTO getOrderDTOByOrderId(@PathVariable("orderId") Integer orderId) {
//        Cache cache = cacheManager.getCache("orderdto");
//        if (cache != null) {
//            return cache.get(String.valueOf(orderId), OrderDTO.class);
//        }
//        return null;
//    }
}
