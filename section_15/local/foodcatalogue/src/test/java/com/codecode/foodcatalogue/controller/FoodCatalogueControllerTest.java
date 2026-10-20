package com.codecode.foodcatalogue.controller;

import com.codecode.foodcatalogue.dto.FoodCataloguePage;
import com.codecode.foodcatalogue.dto.FoodItemDTO;
import com.codecode.foodcatalogue.service.FoodCatalogueService;
import org.apache.coyote.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FoodCatalogueControllerTest {

    @Mock
    private FoodCatalogueService foodCatalogueService;

    @InjectMocks
    private FoodCatalogueController foodCatalogueController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addFoodItem_ShouldReturnCreatedStatus() {
        //Arrange
        FoodItemDTO foodItemDTO = new FoodItemDTO();
        when(foodCatalogueService.addFoodItem(any(FoodItemDTO.class))).thenReturn(foodItemDTO);

        //Act
        ResponseEntity<FoodItemDTO> response = foodCatalogueController.addFoodItem(foodItemDTO);

        //Assert
        verify(foodCatalogueService, times(1)).addFoodItem(foodItemDTO);
        assert response.getStatusCode() == HttpStatus.CREATED;
        assert response.getBody() == foodItemDTO;
    }

    @Test
    void fetchRestaurantAndFoodItems_ShouldReturnOkStatus() {
        //Arrange
        int restaurantId = 123;
        FoodCataloguePage foodCataloguePage = new FoodCataloguePage();
        ResponseEntity<FoodCataloguePage> foodCataloguePageResponseEntity = new ResponseEntity<>(foodCataloguePage, HttpStatus.OK);
        when(foodCatalogueService.fetchFoodCataloguePageDetails(restaurantId)).thenReturn(foodCataloguePageResponseEntity);

        //Act
        ResponseEntity<FoodCataloguePage> response = foodCatalogueController.fetchRestaurantAndFoodItems(restaurantId);

        //Assert
        verify(foodCatalogueService, times(1)).fetchFoodCataloguePageDetails(restaurantId);
        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody() == foodCataloguePage;
    }

    @Test
    void fetchFoodItemListByRestaurantId_ShouldReturnOkStatus() {
        //Arrange
        int restaurantId = 123;
        FoodItemDTO foodItemDTO = new FoodItemDTO();
        List<FoodItemDTO> res = List.of(foodItemDTO);
        when(foodCatalogueService.fetchFoodItemList(restaurantId)).thenReturn(res);

        //Act
        ResponseEntity<List<FoodItemDTO>> response = foodCatalogueController.fetchFoodItemListByRestaurantId(restaurantId);

        //Assert
        verify(foodCatalogueService, times(1)).fetchFoodItemList(restaurantId);
        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody() == res;
    }
}
