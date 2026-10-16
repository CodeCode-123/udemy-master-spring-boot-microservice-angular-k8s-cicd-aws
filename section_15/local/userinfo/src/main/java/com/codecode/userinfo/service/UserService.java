package com.codecode.userinfo.service;

import com.codecode.userinfo.dto.UserDTO;
import com.codecode.userinfo.entity.User;
import com.codecode.userinfo.repository.UserRepo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepo userRepo;

    @Autowired
    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
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
}
