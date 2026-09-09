package com.codecode.userinfo.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "userservice")
@Getter
@Setter
public class UserInfoContactInfoDTO {
    private String message;
    private Map<String, String> contactDetails;
    private List<String> onCallSupport;
}
