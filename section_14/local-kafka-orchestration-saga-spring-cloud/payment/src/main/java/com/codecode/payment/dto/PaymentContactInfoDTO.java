package com.codecode.payment.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "payment")
@Getter
@Setter
public class PaymentContactInfoDTO {
    private String message;
    private Map<String, String> contactDetails;
    private List<String> onCallSupport;
}
