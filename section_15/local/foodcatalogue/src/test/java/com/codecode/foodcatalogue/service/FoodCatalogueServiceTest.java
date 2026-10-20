package com.codecode.foodcatalogue.service;

import com.codecode.foodcatalogue.dto.FoodCataloguePage;
import com.codecode.foodcatalogue.dto.FoodItemDTO;
import com.codecode.foodcatalogue.dto.RestaurantDTO;
import com.codecode.foodcatalogue.entity.FoodItem;
import com.codecode.foodcatalogue.repository.FoodItemRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLEngineResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FoodCatalogueServiceTest {

    @Mock
    private FoodItemRepo foodItemRepo;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private FoodCatalogueService foodCatalogueService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addFoodItem_ShouldSaveFoodItemAndReturnMappedDTO() {
        //Arrange
        FoodItem  foodItem = new FoodItem("test food", "test description", true, 10.0, 1, 2);
        foodItem.setId(1);

        FoodItemDTO foodItemDTO = foodCatalogueService.mapFoodItemToFoodItemDTO(foodItem);
        when(foodItemRepo.save(any(FoodItem.class))).thenReturn(foodItem);

        //Act
        FoodItemDTO result = foodCatalogueService.addFoodItem(foodItemDTO);

        //Assert
        verify(foodItemRepo, times(1)).save(any(FoodItem.class));
        Assertions.assertEquals(foodItemDTO, result);
    }

    @Test
    void fetchFoodCataloguePageDetails_ShouldReturnFoodCataloguePage() {
        //Arrange
        int restaurantId = 123;
        List<FoodItem> foodItemList = List.of(new FoodItem());
        RestaurantDTO restaurantDTO = new RestaurantDTO();
        when(foodItemRepo.findByRestaurantId(restaurantId)).thenReturn(foodItemList);
        when(restTemplate.getForObject(anyString(), eq(RestaurantDTO.class))).thenReturn(restaurantDTO);

        //Act
        ResponseEntity<FoodCataloguePage> result = foodCatalogueService.fetchFoodCataloguePageDetails(restaurantId);

        //Assert
        verify(foodItemRepo, times(1)).findByRestaurantId(restaurantId);
        verify(restTemplate, times(1)).getForObject(anyString(), eq(RestaurantDTO.class));

        Assertions.assertEquals(restaurantDTO, result.getBody().getRestaurantDTO());
    }

    @Test
    void fetchFoodCataloguePageDetails_ShouldReturnNotFound() {
        //Arrange
        int restaurantId = 123;
        List<FoodItem> foodItemList = new ArrayList<>();
        when(foodItemRepo.findByRestaurantId(restaurantId)).thenReturn(foodItemList);
        when(restTemplate.getForObject(anyString(), eq(RestaurantDTO.class))).thenReturn(null);

        //Act
        ResponseEntity<FoodCataloguePage> result = foodCatalogueService.fetchFoodCataloguePageDetails(restaurantId);

        //Assert
        verify(foodItemRepo, times(1)).findByRestaurantId(restaurantId);
        verify(restTemplate, times(1)).getForObject(anyString(), eq(RestaurantDTO.class));

        Assertions.assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        Assertions.assertNull(result.getBody());
    }

    @Test
    void fetchFoodItemList_ShouldReturnFoodItemDTOList() {
        //Arrange
        int restaurantId = 123;
        FoodItem foodItem = new FoodItem();
        foodItem.setId(1);
        foodItem.setItemName("test food");
        foodItem.setPrice(10.0);
        FoodItemDTO foodItemDTO = foodCatalogueService.mapFoodItemToFoodItemDTO(foodItem);

        List<FoodItem> foodItemList = List.of(foodItem);
        List<FoodItemDTO> foodItemDTOList = List.of(foodItemDTO);

        when(foodItemRepo.findByRestaurantId(restaurantId)).thenReturn(foodItemList);

        //Act
        List<FoodItemDTO> result = foodCatalogueService.fetchFoodItemList(restaurantId);

        //Assert
        verify(foodItemRepo, times(1)).findByRestaurantId(restaurantId);
        Assertions.assertEquals(foodItemDTOList, result);
    }
}
