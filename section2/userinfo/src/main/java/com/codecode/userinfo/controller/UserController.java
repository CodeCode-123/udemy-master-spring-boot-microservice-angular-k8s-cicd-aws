package com.codecode.userinfo.controller;

import com.codecode.userinfo.dto.UserDTO;
import com.codecode.userinfo.repository.UserRepo;
import com.codecode.userinfo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/addUser")
    public ResponseEntity<UserDTO> addUser(@RequestBody UserDTO userDTO) {
        UserDTO savedUser = userService.addUser(userDTO);
        return new ResponseEntity<>(savedUser, HttpStatus.OK);
    }

    @GetMapping("/fetchUser/{userId}")
    public ResponseEntity<UserDTO> fetchUserById(@PathVariable Integer userId) {
        return userService.fetchUserById(userId);
    }
}
