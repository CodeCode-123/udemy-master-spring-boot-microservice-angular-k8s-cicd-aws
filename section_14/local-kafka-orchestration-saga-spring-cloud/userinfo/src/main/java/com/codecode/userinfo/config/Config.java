package com.codecode.userinfo.config;

import com.codecode.userinfo.dto.UserInfoContactInfoDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
    @Bean
    public UserInfoContactInfoDTO getUserInfoContactInfoDTO() {
        return new UserInfoContactInfoDTO();
    }
}
