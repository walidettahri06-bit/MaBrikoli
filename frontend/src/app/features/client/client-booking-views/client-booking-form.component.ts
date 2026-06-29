import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ArtisanService } from '../../../core/services/artisan.service';
import { BookingService } from '../../../core/services/booking.service';
import { ArtisanProfileResponse } from '../../../core/models/artisan.model';
import {
  LucideCalendar,
  LucideClock,
  LucideMapPin,
  LucideCheckCircle,
  LucideArrowLeft
} from '@lucide/angular';

@Component({
  selector: 'app-client-booking-form',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    LucideCalendar,
    LucideClock,
    LucideMapPin,
    LucideCheckCircle,
    LucideArrowLeft
  ],
  templateUrl: './client-booking-form.component.html',
  styleUrls: ['./client-booking-form.component.scss']
})
export class ClientBookingFormComponent implements OnInit {
  artisanService = inject(ArtisanService);
  bookingService = inject(BookingService);
  router = inject(Router);
  route = inject(ActivatedRoute);

  artisan = signal<ArtisanProfileResponse | null>(null);
  date = '2026-07-05';
  time = '09:00';
  duration = 2;
  address = 'Appt 14, Rue Ghandi, Casablanca';
  description = '';
  clientNotes = '';
  submitted = false;
  error = '';

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const artisanIdStr = params.get('id');
      if (artisanIdStr) {
        this.artisanService.getArtisanById(Number(artisanIdStr)).subscribe({
          next: (data) => this.artisan.set(data),
          error: (err) => this.error = 'Artisan profile not found.'
        });
      }
    });
  }

  get totalAmount(): number {
    const art = this.artisan();
    return art ? art.hourlyPrice * this.duration : 0;
  }

  onSubmit() {
    const art = this.artisan();
    if (!art) return;

    if (!this.description || !this.address || !this.date) {
      this.error = 'Please fill out all required fields.';
      return;
    }

    const firstCategoryId = art.categories && art.categories.length > 0 ? art.categories[0].id : null;
    if (!firstCategoryId) {
      this.error = 'This artisan has no category specialty mapped.';
      return;
    }

    this.error = '';
    const request = {
      artisanId: art.id,
      categoryId: firstCategoryId,
      description: this.description,
      address: this.address,
      city: art.city,
      bookingDate: this.date,
      preferredTime: this.time,
      clientNotes: this.clientNotes
    };

    this.bookingService.createBooking(request).subscribe({
      next: () => {
        this.submitted = true;
        setTimeout(() => {
          this.router.navigate(['/client/bookings']);
        }, 2000);
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to submit booking request. Please check availability.';
      }
    });
  }

  backToProfile() {
    const art = this.artisan();
    if (art) {
      this.router.navigate([`/client/artisan-details`, art.id]);
    } else {
      this.router.navigate(['/client/search']);
    }
  }

  get artisanName(): string {
    const a = this.artisan();
    return a ? `${a.firstName} ${a.lastName}` : '';
  }

  get artisanAvatar(): string {
    const a = this.artisan();
    return a ? (a.profilePhoto || `https://api.dicebear.com/7.x/pixel-art/svg?seed=${a.firstName}`) : '';
  }
}
