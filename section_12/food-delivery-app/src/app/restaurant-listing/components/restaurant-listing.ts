import { Component, OnInit, inject } from '@angular/core';
import { RestaurantDTO } from '../../shared/model/restaurant';
import { RestaurantService } from '../service/restaurant-service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-restaurant-listing',
  templateUrl: './restaurant-listing.html',
  styleUrl: './restaurant-listing.css',
  imports: [],
})
export class RestaurantListing implements OnInit {
  public restaurantList: RestaurantDTO[];
  private router = inject(Router);
  private restaurantService = inject(RestaurantService);

  ngOnInit() {
    //this.getAllRestaurants();
    //1. Try to load saved data from local storage first
    const savedData = localStorage.getItem('allRestaurantsData');
    if (savedData) {
      //if data is found in the local storage, set it to the restaurantList
      this.restaurantList = JSON.parse(savedData);
    } else {
      //otherwise, load from the backend
      this.getAllRestaurants();
    }
  }

  //constructor(private router: Router, private restaurantService: RestaurantService) { }

  getAllRestaurants() {
    this.restaurantService.getAllRestaurants().subscribe(
      data => {
        this.restaurantList = data;
        //after retrieved the data from the backend, save it to the local storage
        localStorage.setItem('allRestaurantsData', JSON.stringify(this.restaurantList));
      }
    )
  }

  getRandomNumber(min: number, max: number): number {
    return Math.floor(Math.random() * (max - min + 1)) + min;
  }

  getRandomImage(): string {
    const imageCount = 8; // Adjust this number based on the number of images in your asset folder
    const randomIndex = this.getRandomNumber(1, imageCount);
    return `${randomIndex}.jpg`; // Replace with your image filename pattern
  }

  onButtonClick(id: number) {
    this.router.navigate(['/food-catalogue', id]);
  }
}
