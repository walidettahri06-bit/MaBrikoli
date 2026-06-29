import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import { BookingRequest, BookingResponse } from '../models/booking.model';

@Injectable({
  providedIn: 'root'
})
export class BookingService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/bookings`;

  createBooking(request: BookingRequest): Observable<ApiResponse<BookingResponse>> {
    return this.http.post<ApiResponse<BookingResponse>>(this.apiUrl, request);
  }

  getClientBookings(): Observable<BookingResponse[]> {
    return this.http.get<ApiResponse<BookingResponse[]>>(`${this.apiUrl}/client`).pipe(
      map(res => res.data)
    );
  }

  getArtisanBookings(): Observable<BookingResponse[]> {
    return this.http.get<ApiResponse<BookingResponse[]>>(`${this.apiUrl}/artisan`).pipe(
      map(res => res.data)
    );
  }

  acceptBooking(id: number): Observable<ApiResponse<BookingResponse>> {
    return this.http.put<ApiResponse<BookingResponse>>(`${this.apiUrl}/${id}/accept`, {});
  }

  rejectBooking(id: number, notes: string): Observable<ApiResponse<BookingResponse>> {
    return this.http.put<ApiResponse<BookingResponse>>(`${this.apiUrl}/${id}/reject`, { reason: notes });
  }

  completeBooking(id: number): Observable<ApiResponse<BookingResponse>> {
    return this.http.put<ApiResponse<BookingResponse>>(`${this.apiUrl}/${id}/complete`, {});
  }

  getBookingById(id: number): Observable<ApiResponse<BookingResponse>> {
    return this.http.get<ApiResponse<BookingResponse>>(`${this.apiUrl}/${id}`);
  }

  cancelBooking(id: number, notes: string): Observable<ApiResponse<BookingResponse>> {
    return this.http.put<ApiResponse<BookingResponse>>(`${this.apiUrl}/${id}/cancel`, { reason: notes });
  }

  getAllBookings(): Observable<BookingResponse[]> {
    return this.http.get<ApiResponse<BookingResponse[]>>(`${environment.apiUrl}/admin/bookings`).pipe(
      map(res => res.data)
    );
  }
}
