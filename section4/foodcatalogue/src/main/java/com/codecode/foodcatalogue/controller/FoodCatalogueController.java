package com.codecode.foodcatalogue.controller;

import com.codecode.foodcatalogue.dto.FoodCataloguePage;
import com.codecode.foodcatalogue.dto.FoodItemDTO;
import com.codecode.foodcatalogue.service.FoodCatalogueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/foodCatalogue")
@CrossOrigin
public class FoodCatalogueController {
    private final FoodCatalogueService foodCatalogueService;

    @Autowired
    public FoodCatalogueController(FoodCatalogueService foodCatalogueService) {
        this.foodCatalogueService = foodCatalogueService;
    }

    @PostMapping("/addFoodItem")
    public ResponseEntity<FoodItemDTO> addFoodItem(@RequestBody FoodItemDTO foodItemDTO) {
        FoodItemDTO foodItemSaved = foodCatalogueService.addFoodItem(foodItemDTO);
        return new ResponseEntity<>(foodItemSaved, HttpStatus.CREATED);
    }

    @GetMapping("/fetchRestaurantAndFoodItemsById/{restaurantId}")
    public ResponseEntity<FoodCataloguePage> fetchRestaurantAndFoodItems(@PathVariable Integer restaurantId) {
        return foodCatalogueService.fetchFoodCataloguePageDetails(restaurantId);
    }
}
