package com.codecode.restaurantlisting.config;

import com.codecode.restaurantlisting.dto.RestaurantListingContactDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
    @Bean
    public RestaurantListingContactDTO getRestaurantListingContactDTO() {
        return new RestaurantListingContactDTO();
    }
}
