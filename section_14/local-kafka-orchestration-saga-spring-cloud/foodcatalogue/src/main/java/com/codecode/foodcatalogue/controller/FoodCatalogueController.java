package com.codecode.foodcatalogue.controller;


import com.codecode.core.dto.FoodCataloguePage;
import com.codecode.core.dto.FoodItemDTO;
import com.codecode.foodcatalogue.dto.FoodCatalogueContactInfoDTO;
import com.codecode.foodcatalogue.service.FoodCatalogueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/foodCatalogue")
@CrossOrigin
public class FoodCatalogueController {
    private final FoodCatalogueService foodCatalogueService;
    private final FoodCatalogueContactInfoDTO foodCatalogueContactInfoDTO;

    @Value("${build.version}")
    private String buildVersion;

    @Autowired
    public FoodCatalogueController(FoodCatalogueService foodCatalogueService,
                                   FoodCatalogueContactInfoDTO foodCatalogueContactInfoDTO) {
        this.foodCatalogueService = foodCatalogueService;
        this.foodCatalogueContactInfoDTO = foodCatalogueContactInfoDTO;
    }

    @PostMapping("/addFoodItem")
    public ResponseEntity<FoodItemDTO> addFoodItem(@RequestBody FoodItemDTO foodItemDTO) {
        FoodItemDTO foodItemSaved = foodCatalogueService.addFoodItem(foodItemDTO);
        return new ResponseEntity<>(foodItemSaved, HttpStatus.CREATED);
    }

    @GetMapping("/fetchRestaurantAndFoodItemsById/{restaurantId}")
    public ResponseEntity<FoodCataloguePage> fetchRestaurantAndFoodItems(@PathVariable Integer restaurantId) throws Exception {
        return foodCatalogueService.fetchFoodCataloguePageDetails(restaurantId);
    }

    @GetMapping("/fetchFoodItemListByRestaurantId/{restaurantId}")
    public ResponseEntity<List<FoodItemDTO>> fetchFoodItemListByRestaurantId(@PathVariable Integer restaurantId) {
        List<FoodItemDTO> res = foodCatalogueService.fetchFoodItemList(restaurantId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/build-version")
    public ResponseEntity<String> getBuildVersion() {
        return new ResponseEntity<>(buildVersion, HttpStatus.OK);
    }

    @GetMapping("/contact-info")
    public ResponseEntity<FoodCatalogueContactInfoDTO> getContactInfo() {
        return new ResponseEntity<>(foodCatalogueContactInfoDTO, HttpStatus.OK);
    }
}
