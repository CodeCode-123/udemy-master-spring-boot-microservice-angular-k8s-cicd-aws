package com.codecode.foodcatalogue.dto;

import com.codecode.foodcatalogue.entity.FoodItem;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FoodCataloguePage {
    private List<FoodItem> foodItemList;
    private Restaurant restaurant;
}
