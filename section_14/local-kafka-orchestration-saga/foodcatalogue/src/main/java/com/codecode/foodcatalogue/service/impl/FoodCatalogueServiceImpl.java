package com.codecode.foodcatalogue.service.impl;

import com.codecode.core.dto.FoodCataloguePage;
import com.codecode.core.dto.FoodItemDTO;
import com.codecode.core.dto.RestaurantDTO;
import com.codecode.foodcatalogue.entity.FoodItem;
import com.codecode.foodcatalogue.repository.FoodItemRepo;
import com.codecode.foodcatalogue.service.FoodCatalogueService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class FoodCatalogueServiceImpl implements FoodCatalogueService {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final FoodItemRepo foodItemRepo;
    private final ReplyingKafkaTemplate<String, Object, Object> replyingKafkaTemplate;

    @Value("${app.service.url}")
    private String url;


    @Autowired
    public FoodCatalogueServiceImpl(FoodItemRepo foodItemRepo,
                                    ReplyingKafkaTemplate<String, Object, Object> replyingKafkaTemplate) {
        this.foodItemRepo = foodItemRepo;
        this.replyingKafkaTemplate = replyingKafkaTemplate;
    }

    private FoodItem mapFoodItemDTOToFoodItem(FoodItemDTO foodItemDTO) {
        FoodItem foodItem = new FoodItem();
        BeanUtils.copyProperties(foodItemDTO, foodItem);
        return foodItem;
    }

    private FoodItemDTO mapFoodItemToFoodItemDTO(FoodItem foodItem) {
        FoodItemDTO foodItemDTO = new FoodItemDTO();
        BeanUtils.copyProperties(foodItem, foodItemDTO);
        if (foodItem.isVeg()) {
            foodItemDTO.setIsVeg(Boolean.TRUE);
        } else {
            foodItemDTO.setIsVeg(Boolean.FALSE);
        }
        return foodItemDTO;
    }

    public FoodItemDTO addFoodItem(FoodItemDTO foodItemDTO) {
        FoodItem foodItem = mapFoodItemDTOToFoodItem(foodItemDTO);
        foodItemRepo.save(foodItem);
        return mapFoodItemToFoodItemDTO(foodItem);
    }

    public ResponseEntity<FoodCataloguePage> fetchFoodCataloguePageDetails(Integer restaurantId) throws Exception {
        List<FoodItemDTO> foodItemList = fetchFoodItemList(restaurantId);
        RestaurantDTO restaurantDTO = getDataById(restaurantId);
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

    //Kafka request-reply pattern
    public RestaurantDTO getDataById(Integer restaurantId) throws ExecutionException, InterruptedException {
        ProducerRecord<String, Object> record = new ProducerRecord<>("fetch-restaurant-request", restaurantId);
        //Set reply topic header
        record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, "fetch-restaurant-reply".getBytes()));
        RequestReplyFuture<String, Object, Object> sendAndReceive = replyingKafkaTemplate.sendAndReceive(record);
        ConsumerRecord<String, Object> response = sendAndReceive.get();
        return (RestaurantDTO) response.value();
    }

    public List<FoodItemDTO> fetchFoodItemList(Integer restaurantId) {
        List<FoodItem> foodItemList = foodItemRepo.findByRestaurantId(restaurantId);
        List<FoodItemDTO> foodItemDTOList = new ArrayList<>();
        for (FoodItem foodItem : foodItemList) {
            foodItemDTOList.add(mapFoodItemToFoodItemDTO(foodItem));
        }
        return foodItemDTOList;
    }
}
