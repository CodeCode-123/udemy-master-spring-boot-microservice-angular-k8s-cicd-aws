package com.codecode.userinfo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;
    private String userName;
    private String userPassword;
    private String address;
    private  String city;

    public User() {
    }

    public User(String userName, String userPassword, String address, String city) {
        this.userName = userName;
        this.userPassword = userPassword;
        this.address = address;
        this.city = city;
    }
}
