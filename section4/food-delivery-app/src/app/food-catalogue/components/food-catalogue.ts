import { Component, inject, OnInit } from '@angular/core';
import { FoodCataloguePage } from '../../shared/model/foodcataloguepage';
import { FoodItemDTO } from '../../shared/model/fooditem';
import { ActivatedRoute, Router } from '@angular/router';
import { FoodItemService } from '../service/fooditem-service';
import { RestaurantDTO } from '../../shared/model/restaurant';

@Component({
  selector: 'app-food-catalogue',
  templateUrl: './food-catalogue.html',
  styleUrl: './food-catalogue.css',
})
export class FoodCatalogue implements OnInit {
  restaurantId: number;
  foodItemResponse: FoodCataloguePage;
  foodItemCart: FoodItemDTO[] = [];
  orderSummary: FoodCataloguePage;
  foodItemDTOList: FoodItemDTO[] = [];
  restaurantDTO: RestaurantDTO;
  // private dataService = inject(FoodItemService);

  constructor(private route: ActivatedRoute, private foodItemService: FoodItemService, private router: Router) {
  }

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const idParam = this.route.snapshot.paramMap.get('id');
      this.restaurantId = +(idParam ?? 0);
    });

    const savedData = localStorage.getItem('foodItemsByRestaurantData/' + this.restaurantId);
    const savedFoodItemsList = localStorage.getItem('foodItemsListByRestaurantId/' + this.restaurantId);
    if (savedData && savedFoodItemsList) {
      this.foodItemResponse = JSON.parse(savedData);
      this.foodItemDTOList = JSON.parse(savedFoodItemsList);
    } else {
      this.getFoodItemsByRestaurant(this.restaurantId);
      this.getFoodItemsList(this.restaurantId);
    }
  }

  getFoodItemsByRestaurant(restaurant: number) {
    this.foodItemService.getFoodItemsByRestaurant(restaurant).subscribe(
      data => {
        this.foodItemResponse = data;
        localStorage.setItem('foodItemsByRestaurantData/' + this.restaurantId, JSON.stringify(this.foodItemResponse));
      }
    )
  }

  getFoodItemsList(restaurant: number) {
    this.foodItemService.getFoodItemsListByRestaurant(restaurant).subscribe(
      data => {
        this.foodItemDTOList = data;
        localStorage.setItem('foodItemsListByRestaurantId/' + this.restaurantId, JSON.stringify(this.foodItemDTOList));
      }
    );
  }

  increment(food: any) {
    food.quantity++;
    const index = this.foodItemCart.findIndex(item => item.id === food.id);
    if (index === -1) {
      // If record does not exist, add it to the array
      this.foodItemCart.push(food);
    } else {
      // If record exists, update it in the array
      this.foodItemCart[index] = food;
    }
  }

  decrement(food: any) {
    if (food.quantity > 0) {
      food.quantity--;

      const index =  this.foodItemCart.findIndex(item => item.id === food.id);
      //if the quantity == 0, remove the item from the array using splice()
      if (this.foodItemCart[index].quantity == 0) {
        this.foodItemCart.splice(index, 1);
      } else {
        // If record exists, update it in the array
        this.foodItemCart[index] = food;
      }
    }
  }

  onCheckOut() {
    this.foodItemCart;
    this.orderSummary = {
      foodItemsList: [],
      restaurantDTO: null as any
    }
    this.orderSummary.foodItemsList = this.foodItemCart;
    this.orderSummary.restaurantDTO = this.foodItemResponse.restaurantDTO;
    //turning a Java object to a string and display the text parameter when the page loads
    this.router.navigate(['/orderSummary'], { queryParams: { data: JSON.stringify(this.orderSummary)}});
  }
}


