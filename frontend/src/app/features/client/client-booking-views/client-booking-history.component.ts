import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BookingService } from '../../../core/services/booking.service';
import { BookingResponse } from '../../../core/models/booking.model';
import { BookingCardComponent } from '../../../shared/components/booking-card/booking-card.component';
import { LucideCalendar } from '@lucide/angular';

@Component({
  selector: 'app-client-booking-history',
  standalone: true,
  imports: [
    CommonModule,
    BookingCardComponent,
    LucideCalendar
  ],
  templateUrl: './client-booking-history.component.html',
  styleUrls: ['./client-booking-history.component.scss']
})
export class ClientBookingHistoryComponent implements OnInit {
  bookingService = inject(BookingService);

  bookings = signal<BookingResponse[]>([]);
  error = '';

  ngOnInit() {
    this.loadBookings();
  }

  loadBookings() {
    this.bookingService.getClientBookings().subscribe({
      next: (list) => this.bookings.set(list),
      error: (err) => this.error = 'Failed to load bookings.'
    });
  }
}
