import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import { ArtisanApplicationRequest, ArtisanApplicationResponse } from '../models/application.model';

@Injectable({
  providedIn: 'root'
})
export class ApplicationService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}`;

  submitApplication(request: ArtisanApplicationRequest): Observable<ApiResponse<ArtisanApplicationResponse>> {
    return this.http.post<ApiResponse<ArtisanApplicationResponse>>(`${this.apiUrl}/artisan-applications`, request);
  }

  getMyApplications(): Observable<ApiResponse<ArtisanApplicationResponse[]>> {
    return this.http.get<ApiResponse<ArtisanApplicationResponse[]>>(`${this.apiUrl}/artisan-applications/me`);
  }

  getAllApplications(): Observable<ApiResponse<ArtisanApplicationResponse[]>> {
    return this.http.get<ApiResponse<ArtisanApplicationResponse[]>>(`${this.apiUrl}/admin/artisan-applications`);
  }

  getApplicationById(id: number): Observable<ApiResponse<ArtisanApplicationResponse>> {
    return this.http.get<ApiResponse<ArtisanApplicationResponse>>(`${this.apiUrl}/admin/artisan-applications/${id}`);
  }

  approveApplication(id: number): Observable<ApiResponse<ArtisanApplicationResponse>> {
    return this.http.put<ApiResponse<ArtisanApplicationResponse>>(`${this.apiUrl}/admin/artisan-applications/${id}/approve`, {});
  }

  rejectApplication(id: number, reason: string): Observable<ApiResponse<ArtisanApplicationResponse>> {
    return this.http.put<ApiResponse<ArtisanApplicationResponse>>(`${this.apiUrl}/admin/artisan-applications/${id}/reject`, { reason });
  }
}
