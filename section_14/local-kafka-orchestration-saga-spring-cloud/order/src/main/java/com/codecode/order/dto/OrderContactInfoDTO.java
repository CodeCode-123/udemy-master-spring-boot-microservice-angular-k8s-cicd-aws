package com.codecode.order.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "order")
@Getter
@Setter
public class OrderContactInfoDTO {
    private String message;
    private Map<String, String> contactDetails;
    private List<String> onCallSupport;
}
