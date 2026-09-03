package com.codecode.message.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;

import java.util.concurrent.TimeUnit;

@Configuration
public class Config {

    //Caffeine Cache Configuration
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        //Define bounded custom specs per cache name
        cacheManager.registerCustomCache("orderdto", Caffeine.newBuilder()
                .maximumSize(5_000)
                .expireAfterWrite(15, TimeUnit.MINUTES)
                .build());
        return cacheManager;
    }

}
