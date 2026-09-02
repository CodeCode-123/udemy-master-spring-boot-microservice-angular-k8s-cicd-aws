package com.codecode.foodcatalogue.service;

import com.codecode.foodcatalogue.dto.FoodCataloguePage;
import com.codecode.foodcatalogue.dto.FoodItemDTO;
import com.codecode.foodcatalogue.dto.RestaurantDTO;
import com.codecode.foodcatalogue.entity.FoodItem;
import com.codecode.foodcatalogue.repository.FoodItemRepo;
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
public class FoodCatalogueService {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final FoodItemRepo foodItemRepo;
    private final ReplyingKafkaTemplate<String, String, String> replyingKafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.service.url}")
    private String url;


    @Autowired
    public FoodCatalogueService(FoodItemRepo foodItemRepo,
                                ReplyingKafkaTemplate<String, String, String> replyingKafkaTemplate,
                                ObjectMapper objectMapper) {
        this.foodItemRepo = foodItemRepo;
        this.replyingKafkaTemplate = replyingKafkaTemplate;
        this.objectMapper = objectMapper;
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
        ProducerRecord<String, String> record = new ProducerRecord<>("fetch-restaurant-request", String.valueOf(restaurantId));
        //Set reply topic header
        record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, "fetch-restaurant-reply".getBytes()));
        RequestReplyFuture<String, String, String> sendAndReceive = replyingKafkaTemplate.sendAndReceive(record);
        ConsumerRecord<String, String> response = sendAndReceive.get();
        return objectMapper.readValue(response.value(), RestaurantDTO.class);
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
