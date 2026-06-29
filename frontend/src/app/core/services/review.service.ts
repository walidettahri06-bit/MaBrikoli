import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import { ReviewRequest, ReviewResponse } from '../models/review.model';

@Injectable({
  providedIn: 'root'
})
export class ReviewService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/reviews`;

  getReviewsForArtisan(artisanId: number): Observable<ReviewResponse[]> {
    return this.http.get<ApiResponse<ReviewResponse[]>>(`${this.apiUrl}/artisan/${artisanId}`).pipe(
      map(res => res.data)
    );
  }

  submitReview(request: ReviewRequest): Observable<ApiResponse<ReviewResponse>> {
    return this.http.post<ApiResponse<ReviewResponse>>(this.apiUrl, request);
  }
}
