package com.codecode.userinfo.controller;


import com.codecode.core.dto.UserDTO;
import com.codecode.userinfo.dto.UserInfoContactInfoDTO;
import com.codecode.userinfo.repository.UserRepo;
import com.codecode.userinfo.service.UserService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final UserInfoContactInfoDTO userInfoContactInfoDTO;

    @Value("${build.version}")
    private String buildVersion;

    @Autowired
    public UserController(UserService userService, UserInfoContactInfoDTO userInfoContactInfoDTO) {
        this.userService = userService;
        this.userInfoContactInfoDTO = userInfoContactInfoDTO;
    }

    @PostMapping("/addUser")
    public ResponseEntity<UserDTO> addUser(@RequestBody UserDTO userDTO) {
        UserDTO savedUser = userService.addUser(userDTO);
        return new ResponseEntity<>(savedUser, HttpStatus.OK);
    }

    @GetMapping("/fetchUserById/{userId}")
    public ResponseEntity<UserDTO> fetchUserById(@PathVariable Integer userId) {
        return userService.fetchUserById(userId);
    }

    @GetMapping("/contact-info")
    public ResponseEntity<UserInfoContactInfoDTO> getContactInfo() {
        return new ResponseEntity<>(userInfoContactInfoDTO, HttpStatus.OK);
    }

    @GetMapping("/build-version")
    public ResponseEntity<String> getBuildVersion() {
        return new ResponseEntity<>(buildVersion, HttpStatus.OK);
    }
}
