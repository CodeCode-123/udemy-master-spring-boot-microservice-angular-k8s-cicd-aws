package com.codecode.userinfo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private int userId;
    private String userName;
    private String userPassword;
    private String address;
    private  String city;
}
