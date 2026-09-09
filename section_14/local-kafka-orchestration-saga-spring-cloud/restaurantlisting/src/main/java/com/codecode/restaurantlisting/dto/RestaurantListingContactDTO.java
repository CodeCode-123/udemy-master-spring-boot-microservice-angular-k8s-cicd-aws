package com.codecode.restaurantlisting.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "restaurantlisting")
@Getter
@Setter
public class RestaurantListingContactDTO {
    private String message;
    private Map<String, String> contactDetails;
    private List<String> onCallSupport;
}
