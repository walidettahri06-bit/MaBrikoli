import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { BookingService } from '../../../core/services/booking.service';
import { ArtisanService } from '../../../core/services/artisan.service';
import { AuthService } from '../../../core/services/auth.service';
import { BookingResponse } from '../../../core/models/booking.model';
import { ArtisanProfileResponse } from '../../../core/models/artisan.model';
import { BookingCardComponent } from '../../../shared/components/booking-card/booking-card.component';
import {
  LucideCalendar,
  LucideHeart,
  LucideShieldCheck,
  LucideSearch,
  LucideChevronRight
} from '@lucide/angular';

@Component({
  selector: 'app-client-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    BookingCardComponent,
    LucideCalendar,
    LucideHeart,
    LucideShieldCheck,
    LucideSearch,
    LucideChevronRight
  ],
  templateUrl: './client-dashboard.component.html',
  styleUrls: ['./client-dashboard.component.scss']
})
export class ClientDashboardComponent implements OnInit {
  bookingService = inject(BookingService);
  artisanService = inject(ArtisanService);
  authService = inject(AuthService);
  router = inject(Router);

  bookings = signal<BookingResponse[]>([]);
  favoriteArtisans = signal<ArtisanProfileResponse[]>([]);
  favoritesCount = signal(0);

  activeBookings = computed(() => {
    return this.bookings().filter(b => b.status === 'PENDING' || b.status === 'CONFIRMED');
  });

  ngOnInit() {
    this.loadDashboardData();
  }

  loadDashboardData() {
    // Load Bookings
    this.bookingService.getClientBookings().subscribe({
      next: (list) => this.bookings.set(list),
      error: (err) => console.error('Failed to load bookings:', err)
    });

    // Load Favorites
    const favsJson = localStorage.getItem('favorites');
    if (favsJson) {
      try {
        const favIds = JSON.parse(favsJson) as number[];
        this.favoritesCount.set(favIds.length);
        if (favIds.length > 0) {
          this.artisanService.getArtisans().subscribe({
            next: (list) => {
              const matched = list.filter(art => favIds.includes(art.id));
              this.favoriteArtisans.set(matched);
            }
          });
        } else {
          this.favoriteArtisans.set([]);
        }
      } catch (e) {
        this.favoriteArtisans.set([]);
      }
    }
  }

  navigateToSearch() {
    this.router.navigate(['/client/search']);
  }

  navigateToBookings() {
    this.router.navigate(['/client/bookings']);
  }

  navigateToArtisanDetails(id: number) {
    this.router.navigate([`/client/artisan-details`, id]);
  }
}
