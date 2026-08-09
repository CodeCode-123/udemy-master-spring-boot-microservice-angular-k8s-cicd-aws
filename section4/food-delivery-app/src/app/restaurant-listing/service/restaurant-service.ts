import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { API_URL_RL } from '../../constants/url';

@Injectable({
    providedIn: 'root'
})
export class RestaurantService {
    private apiUrl = API_URL_RL + '/restaurant/fetchAllRestaurants';
    
    constructor(private http: HttpClient) { }

    //fetch data from the backend, RxJS stream
    getAllRestaurants(): Observable<any> {
        return this.http.get<any>(`${this.apiUrl}`)
        .pipe(
            catchError(this.handleError)
        );
    }

    private handleError(error: any) {
        console.error('An error occurred:', error);
        return throwError(() => new Error(error.message || error));
    }
}
