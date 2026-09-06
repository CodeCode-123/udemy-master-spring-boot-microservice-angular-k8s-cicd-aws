package com.codecode.userinfo.service;

import com.codecode.core.dto.UserDTO;
import com.codecode.userinfo.entity.User;
import com.codecode.userinfo.repository.UserRepo;
import jakarta.ws.rs.NotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepo userRepo;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public UserService(UserRepo userRepo, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.userRepo = userRepo;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public UserDTO addUser(UserDTO userDTO) {
        User user = mapUserDTOToUser(userDTO);
        //return userDTO with created userId saved in the database
        return mapUserToUserDTO(userRepo.save(user));
    }

    private User mapUserDTOToUser(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        return user;
    }

    private UserDTO mapUserToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }

    public ResponseEntity<UserDTO> fetchUserById(Integer userId) {
        Optional<User> fetchedUser = userRepo.findById(userId);
        if (fetchedUser.isPresent()) {
            UserDTO userDTO = mapUserToUserDTO(fetchedUser.get());
            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @KafkaListener(topics = "fetch-user-request")
    @SendTo
    public String handleRequest(Integer userId) {
        Optional<User> userOptional = userRepo.findById(userId);
        if (userOptional.isEmpty()) {
            throw new NotFoundException("User is not Found by userId: " + userId);
        }
        User user = userOptional.get();
        UserDTO userDTO = convertToUserDTO(user);
        return objectMapper.writeValueAsString(userDTO);
    }

    private UserDTO convertToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(user.getUserId());
        userDTO.setUserName(user.getUserName());
        userDTO.setUserPassword(user.getUserPassword());
        userDTO.setCity(user.getCity());
        userDTO.setAddress(user.getAddress());
        return userDTO;
    }

}
