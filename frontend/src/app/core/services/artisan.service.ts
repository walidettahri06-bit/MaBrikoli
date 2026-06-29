import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import { ArtisanProfileResponse } from '../models/artisan.model';

@Injectable({
  providedIn: 'root'
})
export class ArtisanService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/artisan-profiles`;

  /**
   * Fetches all registered and verified artisan profiles.
   */
  getArtisans(): Observable<ArtisanProfileResponse[]> {
    return this.http.get<ApiResponse<ArtisanProfileResponse[]>>(this.apiUrl).pipe(
      map(res => res.data)
    );
  }

  /**
   * Fetches a single artisan profile details by profile ID.
   */
  getArtisanById(id: number): Observable<ArtisanProfileResponse> {
    return this.http.get<ApiResponse<ArtisanProfileResponse>>(`${this.apiUrl}/${id}`).pipe(
      map(res => res.data)
    );
  }

  /**
   * Fetches the current artisan's own profile.
   */
  getMyProfile(): Observable<ArtisanProfileResponse> {
    return this.http.get<ApiResponse<ArtisanProfileResponse>>(`${this.apiUrl}/me`).pipe(
      map(res => res.data)
    );
  }

  /**
   * Updates the current artisan's own profile.
   */
  updateProfile(request: any): Observable<ApiResponse<ArtisanProfileResponse>> {
    return this.http.put<ApiResponse<ArtisanProfileResponse>>(`${this.apiUrl}/me`, request);
  }
}
