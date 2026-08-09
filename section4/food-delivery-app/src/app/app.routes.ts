import { Routes } from '@angular/router';
import { RestaurantListing } from './restaurant-listing/components/restaurant-listing';
import { FoodCatalogue } from './food-catalogue/components/food-catalogue';

export const routes: Routes = [
    { path: '', component: RestaurantListing },
    { path: 'food-catalogue/:id', component: FoodCatalogue }
];
