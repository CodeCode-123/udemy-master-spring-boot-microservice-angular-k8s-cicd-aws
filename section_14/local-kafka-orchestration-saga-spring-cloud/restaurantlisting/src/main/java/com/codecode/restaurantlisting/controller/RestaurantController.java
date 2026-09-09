package com.codecode.restaurantlisting.controller;

import com.codecode.core.dto.RestaurantDTO;
import com.codecode.restaurantlisting.dto.RestaurantListingContactDTO;
import com.codecode.restaurantlisting.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurant")
@CrossOrigin
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final RestaurantListingContactDTO restaurantListingContactDTO;

    @Value("${build.version}")
    private String buildVersion;

    @Autowired
    public RestaurantController(RestaurantService restaurantService, RestaurantListingContactDTO restaurantListingContactDTO) {
        this.restaurantService = restaurantService;
        this.restaurantListingContactDTO = restaurantListingContactDTO;
    }

    @GetMapping("/fetchAllRestaurants")
    public ResponseEntity<List<RestaurantDTO>> fetchAllRestaurants() {
        List<RestaurantDTO> allRestaurants = restaurantService.findAllRestaurants();
        return new ResponseEntity<>(allRestaurants, HttpStatus.OK);
    }

    @PostMapping("/addRestaurant")
    public ResponseEntity<RestaurantDTO> saveRestaurant(@RequestBody RestaurantDTO restaurantDTO) {
        RestaurantDTO restaurantAdded = restaurantService.addRestaurantInDB(restaurantDTO);
        return new ResponseEntity<>(restaurantAdded, HttpStatus.CREATED);
    }

    @GetMapping("/fetchById/{id}")
    public ResponseEntity<RestaurantDTO> findRestaurantById(@PathVariable Integer id) {
        return restaurantService.fetchRestaurantById(id);
    }

    @GetMapping("/build-version")
    public ResponseEntity<String> getBuildVersion() {
        return new ResponseEntity<>(buildVersion, HttpStatus.OK);
    }

    @GetMapping("/contact-info")
    public ResponseEntity<RestaurantListingContactDTO> getContactInfo() {
        return new ResponseEntity<>(restaurantListingContactDTO, HttpStatus.OK);
    }
}
