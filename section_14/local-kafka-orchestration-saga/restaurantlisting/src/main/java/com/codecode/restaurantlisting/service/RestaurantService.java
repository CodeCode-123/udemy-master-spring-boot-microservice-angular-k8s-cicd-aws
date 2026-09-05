package com.codecode.restaurantlisting.service;

import com.codecode.restaurantlisting.dto.RestaurantDTO;
import com.codecode.restaurantlisting.entity.Restaurant;
import com.codecode.restaurantlisting.mapper.RestaurantMapper;
import com.codecode.restaurantlisting.repository.RestaurantRepo;
import jakarta.ws.rs.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;


@Service
public class RestaurantService {
    private final RestaurantRepo restaurantRepo;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public RestaurantService(RestaurantRepo restaurantRepo, KafkaTemplate<String, Object> kafkaTemplate, ObjectMapper objectMapper) {
        this.restaurantRepo = restaurantRepo;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public List<RestaurantDTO> findAllRestaurants() {
        List<Restaurant> restaurants = restaurantRepo.findAll();
        List<RestaurantDTO> restaurantDTOList = restaurants.stream().map(restaurant ->
                RestaurantMapper.INSTANCE
                        .mapRestaurantToRestaurantDTO(restaurant)).toList();
        return restaurantDTOList;
    }

    @KafkaListener(topics = "fetch-restaurant-request")
    @SendTo("fetch-restaurant-reply")
    public String handleRequest(Integer restaurantId) {
        Optional<Restaurant> restaurantOptional = restaurantRepo.findById(restaurantId);
        if (restaurantOptional.isEmpty()) {
            throw new NotFoundException("RestaurantDTO is not Found by restaurantId: " + restaurantId);
        }
        Restaurant restaurant = restaurantOptional.get();
        RestaurantDTO restaurantDTO = convertToRestaurantDTO(restaurant);
        return objectMapper.writeValueAsString(restaurantDTO);
    }

    public RestaurantDTO addRestaurantInDB(RestaurantDTO restaurantDTO) {
        Restaurant savedRestaurant = restaurantRepo.save(RestaurantMapper.INSTANCE
                .mapRestaurantDTOToRestaurant(restaurantDTO));
        return RestaurantMapper.INSTANCE.mapRestaurantToRestaurantDTO(savedRestaurant);
    }

    public ResponseEntity<RestaurantDTO> fetchRestaurantById(Integer id) {
        Optional<Restaurant> restaurant = restaurantRepo.findById(id);
        if (restaurant.isPresent()) {
            return new ResponseEntity<>(RestaurantMapper.INSTANCE.mapRestaurantToRestaurantDTO(restaurant.get()),
                    HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    private RestaurantDTO convertToRestaurantDTO(Restaurant restaurant) {
        RestaurantDTO restaurantDTO = new RestaurantDTO();
        restaurantDTO.setId(restaurant.getId());
        restaurantDTO.setName(restaurant.getName());
        restaurantDTO.setCity(restaurant.getCity());
        restaurantDTO.setAddress(restaurant.getAddress());
        restaurantDTO.setRestaurantDescription(restaurant.getRestaurantDescription());
        return restaurantDTO;
    }
}
