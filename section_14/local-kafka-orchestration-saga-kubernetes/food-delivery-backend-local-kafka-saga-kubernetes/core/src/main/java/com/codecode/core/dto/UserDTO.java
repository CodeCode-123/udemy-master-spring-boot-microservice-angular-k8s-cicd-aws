package com.codecode.core.dto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserDTO {
    private int userId;
    private String userName;
    private String userPassword;
    private String address;
    private  String city;
}
