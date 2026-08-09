import { Component, OnInit } from '@angular/core';
import { FoodCataloguePage } from '../../shared/model/foodcataloguepage';
import { FoodItemDTO } from '../../shared/model/fooditem';
import { ActivatedRoute, Router } from '@angular/router';
import { FoodItemService } from '../service/fooditem-service';
import { RestaurantDTO } from '../../shared/model/restaurant';
import { switchMap } from 'rxjs';

@Component({
  selector: 'app-food-catalogue',
  imports: [],
  templateUrl: './food-catalogue.html',
  styleUrl: './food-catalogue.css',
})
export class FoodCatalogue implements OnInit {
  restaurantId: number;
  foodItemResponse: FoodCataloguePage;
  foodItemCart: FoodItemDTO[] = [];
  orderSummary: FoodCataloguePage;

  constructor(private route: ActivatedRoute, private foodItemService: FoodItemService, private router: Router) {
  }

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      this.restaurantId = +(params.get('id') ?? 0);
    });

    const savedData = localStorage.getItem('foodItemsByRestaurantData/' + this.restaurantId);
    if (savedData) {
      this.foodItemResponse = JSON.parse(savedData);
      const savedMenu = localStorage.getItem('foodMenuByRestaurantData/' + this.restaurantId);
      if (savedMenu) {
        this.foodItemResponse.foodItemsList = JSON.parse(savedMenu);
      }
    } else {
      this.getFoodItemsByRestaurant(this.restaurantId);
    }
  }

  getFoodItemsByRestaurant(restaurant: number) {
    this.foodItemService.getFoodItemsByRestaurant(restaurant).subscribe(
      data => {
        this.foodItemResponse = data;
        localStorage.setItem('foodItemsByRestaurantData/' + this.restaurantId, JSON.stringify(this.foodItemResponse));
        localStorage.setItem('foodMenuByRestaurantData/' + this.restaurantId, JSON.stringify(this.foodItemResponse.foodItemsList));
      }
    )
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


