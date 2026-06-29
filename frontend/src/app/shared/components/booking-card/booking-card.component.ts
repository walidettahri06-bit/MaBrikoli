import { Component, input, output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { BookingResponse } from '../../../core/models/booking.model';
import { AuthService } from '../../../core/services/auth.service';
import { BookingService } from '../../../core/services/booking.service';
import {
  LucideCalendar,
  LucideClock,
  LucideMapPin,
  LucideCheckCircle,
  LucideXCircle,
  LucideChevronRight,
  LucideAlertCircle,
  LucideMessageSquare
} from '@lucide/angular';

@Component({
  selector: 'app-booking-card',
  standalone: true,
  imports: [
    CommonModule,
    LucideCalendar,
    LucideClock,
    LucideMapPin,
    LucideCheckCircle,
    LucideXCircle,
    LucideChevronRight,
    LucideAlertCircle,
    LucideMessageSquare
  ],
  templateUrl: './booking-card.component.html',
  styleUrls: ['./booking-card.component.scss']
})
export class BookingCardComponent {
  booking = input.required<BookingResponse>();
  statusChanged = output<void>();

  authService = inject(AuthService);
  bookingService = inject(BookingService);
  router = inject(Router);

  get isClient(): boolean {
    return this.authService.currentRole() === 'Client';
  }

  get isArtisan(): boolean {
    return this.authService.currentRole() === 'Artisan';
  }

  get isAdmin(): boolean {
    return this.authService.currentRole() === 'Admin';
  }

  getStatusStyle(): string {
    switch (this.booking().status) {
      case 'PENDING':
        return 'bg-amber-50 text-amber-700 border-amber-100';
      case 'CONFIRMED':
        return 'bg-blue-50 text-blue-700 border-blue-100';
      case 'COMPLETED':
        return 'bg-emerald-50 text-emerald-700 border-emerald-100';
      case 'CANCELLED':
      case 'REJECTED':
        return 'bg-rose-50 text-rose-700 border-rose-100';
      default:
        return 'bg-slate-50 text-slate-700 border-slate-100';
    }
  }

  handleCardClick() {
    if (this.isClient) {
      this.router.navigate([`/client/bookings`]);
    } else if (this.isArtisan) {
      this.router.navigate([`/artisan/bookings`]);
    } else if (this.isAdmin) {
      this.router.navigate([`/admin/dashboard`]);
    }
  }

  handleCancel(e: MouseEvent) {
    e.stopPropagation();
    const reason = prompt('Please enter cancellation reason:');
    if (reason === null) return;
    this.bookingService.cancelBooking(this.booking().id, reason).subscribe({
      next: () => this.statusChanged.emit()
    });
  }

  handleAccept(e: MouseEvent) {
    e.stopPropagation();
    this.bookingService.acceptBooking(this.booking().id).subscribe({
      next: () => this.statusChanged.emit()
    });
  }

  handleReject(e: MouseEvent) {
    e.stopPropagation();
    const reason = prompt('Please enter decline reason:');
    if (reason === null) return;
    this.bookingService.rejectBooking(this.booking().id, reason).subscribe({
      next: () => this.statusChanged.emit()
    });
  }

  handleComplete(e: MouseEvent) {
    e.stopPropagation();
    this.bookingService.completeBooking(this.booking().id).subscribe({
      next: () => this.statusChanged.emit()
    });
  }

  handleReview(e: MouseEvent) {
    e.stopPropagation();
    this.router.navigate([`/client/bookings`]);
  }
}
