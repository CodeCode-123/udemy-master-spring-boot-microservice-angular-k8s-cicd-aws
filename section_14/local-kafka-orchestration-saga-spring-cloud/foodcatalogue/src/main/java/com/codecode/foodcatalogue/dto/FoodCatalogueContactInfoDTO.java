package com.codecode.foodcatalogue.dto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "foodcatalogue-service")
@Getter
@Setter
public class FoodCatalogueContactInfoDTO {
    private String message;
    private Map<String, String> contactDetails;
    private List<String> onCallSupport;
}
