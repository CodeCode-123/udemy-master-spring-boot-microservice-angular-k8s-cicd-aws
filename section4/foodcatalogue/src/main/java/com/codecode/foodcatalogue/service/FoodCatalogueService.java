package com.codecode.foodcatalogue.service;

import com.codecode.foodcatalogue.dto.FoodCataloguePage;
import com.codecode.foodcatalogue.dto.FoodItemDTO;
import com.codecode.foodcatalogue.dto.RestaurantDTO;
import com.codecode.foodcatalogue.entity.FoodItem;
import com.codecode.foodcatalogue.repository.FoodItemRepo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class FoodCatalogueService {
    private final FoodItemRepo foodItemRepo;
    private final RestTemplate restTemplate;
    @Value("${app.service.url}")
    private String url;


    @Autowired
    public FoodCatalogueService(FoodItemRepo foodItemRepo, RestTemplate restTemplate) {
        this.foodItemRepo = foodItemRepo;
        this.restTemplate = restTemplate;
    }

    private FoodItem mapFoodItemDTOToFoodItem(FoodItemDTO foodItemDTO) {
        FoodItem foodItem = new FoodItem();
        BeanUtils.copyProperties(foodItemDTO, foodItem);
        return foodItem;
    }

    private FoodItemDTO mapFoodItemToFoodItemDTO(FoodItem foodItem) {
        FoodItemDTO foodItemDTO = new FoodItemDTO();
        BeanUtils.copyProperties(foodItem, foodItemDTO);
        return foodItemDTO;
    }

    public FoodItemDTO addFoodItem(FoodItemDTO foodItemDTO) {
        FoodItem foodItem = mapFoodItemDTOToFoodItem(foodItemDTO);
        foodItemRepo.save(foodItem);
        return mapFoodItemToFoodItemDTO(foodItem);
    }

    public ResponseEntity<FoodCataloguePage> fetchFoodCataloguePageDetails(Integer restaurantId) {
        List<FoodItemDTO> foodItemList = fetchFoodItemList(restaurantId);
        RestaurantDTO restaurantDTO = fetchRestaurantDetailsFromRestaurantMS(restaurantId);
        if (restaurantDTO != null) {
            FoodCataloguePage foodCataloguePage = createFoodCataloguePage(foodItemList, restaurantDTO);
            return new ResponseEntity<>(foodCataloguePage, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    private FoodCataloguePage createFoodCataloguePage(List<FoodItemDTO> foodItemList, RestaurantDTO restaurantDTO) {
        FoodCataloguePage foodCataloguePage = new FoodCataloguePage();
        foodCataloguePage.setFoodItemList(foodItemList);
        foodCataloguePage.setRestaurantDTO(restaurantDTO);
        return foodCataloguePage;
    }

    //fetch data from another service registered to the Eureka Server
    private RestaurantDTO fetchRestaurantDetailsFromRestaurantMS(Integer restaurantId) {
        return restTemplate.getForObject(url + restaurantId, RestaurantDTO.class);
    }

    private List<FoodItemDTO> fetchFoodItemList(Integer restaurantId) {
        List<FoodItem> foodItemList = foodItemRepo.findByRestaurantId(restaurantId);
        List<FoodItemDTO> foodItemDTOList = new ArrayList<>();
        for (FoodItem foodItem : foodItemList) {
            foodItemDTOList.add(mapFoodItemToFoodItemDTO(foodItem));
        }
        return foodItemDTOList;
    }
}
