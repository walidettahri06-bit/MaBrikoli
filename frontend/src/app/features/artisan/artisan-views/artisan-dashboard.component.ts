import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { ArtisanService } from '../../../core/services/artisan.service';
import { BookingService } from '../../../core/services/booking.service';
import { ReviewService } from '../../../core/services/review.service';
import { ApplicationService } from '../../../core/services/application.service';
import { ArtisanProfileResponse } from '../../../core/models/artisan.model';
import { BookingResponse } from '../../../core/models/booking.model';
import { ReviewResponse } from '../../../core/models/review.model';
import { ArtisanApplicationResponse } from '../../../core/models/application.model';
import { BookingCardComponent } from '../../../shared/components/booking-card/booking-card.component';
import { FormsModule } from '@angular/forms';
import {
  LucideCalendar,
  LucideClock,
  LucideMapPin,
  LucideCheckCircle,
  LucideChevronRight,
  LucideStar,
  LucideShieldCheck,
  LucideFolderCheck,
  LucideCreditCard,
  LucideSparkles,
  LucideAlertCircle,
  LucideAlertTriangle
} from '@lucide/angular';

@Component({
  selector: 'app-artisan-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    BookingCardComponent,
    LucideCalendar,
    LucideClock,
    LucideMapPin,
    LucideCheckCircle,
    LucideChevronRight,
    LucideStar,
    LucideShieldCheck,
    LucideFolderCheck,
    LucideCreditCard,
    LucideSparkles,
    LucideAlertCircle,
    LucideAlertTriangle
  ],
  templateUrl: './artisan-dashboard.component.html',
  styleUrls: ['./artisan-dashboard.component.scss']
})
export class ArtisanDashboardComponent implements OnInit {
  authService = inject(AuthService);
  artisanService = inject(ArtisanService);
  bookingService = inject(BookingService);
  reviewService = inject(ReviewService);
  applicationService = inject(ApplicationService);
  router = inject(Router);

  artisanProfile = signal<ArtisanProfileResponse | null>(null);
  bookings = signal<BookingResponse[]>([]);
  reviews = signal<ReviewResponse[]>([]);
  applications = signal<ArtisanApplicationResponse[]>([]);

  isArtisanRole = computed(() => this.authService.currentRole() === 'Artisan');

  pendingBookings = computed(() => {
    return this.bookings().filter(b => b.status === 'PENDING');
  });

  activeBookings = computed(() => {
    return this.bookings().filter(b => b.status === 'CONFIRMED');
  });

  totalEarnings = computed(() => {
    return this.bookings()
      .filter(b => b.status === 'COMPLETED')
      .reduce((sum, b) => sum + (b.finalPrice || b.estimatedPrice || 0), 0);
  });

  completedCount = computed(() => {
    return this.bookings().filter(b => b.status === 'COMPLETED').length;
  });

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    if (this.isArtisanRole()) {
      this.artisanService.getMyProfile().subscribe({
        next: (profile) => {
          this.artisanProfile.set(profile);
          this.loadReviews(profile.id);
        },
        error: (err) => console.error('Failed to load artisan profile:', err)
      });

      this.bookingService.getArtisanBookings().subscribe({
        next: (list) => this.bookings.set(list),
        error: (err) => console.error('Failed to load artisan bookings:', err)
      });
    } else {
      // If client role, check if they have submitted an application
      this.applicationService.getMyApplications().subscribe({
        next: (res) => this.applications.set(res?.data || []),
        error: (err) => console.error('Failed to load applications:', err)
      });
    }
  }

  loadReviews(artisanId: number) {
    this.reviewService.getReviewsForArtisan(artisanId).subscribe({
      next: (list) => this.reviews.set(list)
    });
  }

  handleAvailabilityChange(available: boolean) {
    const profile = this.artisanProfile();
    if (!profile) return;

    const request = {
      bio: profile.bio,
      yearsOfExperience: profile.yearsOfExperience,
      city: profile.city,
      address: profile.address,
      hourlyPrice: profile.hourlyPrice,
      available: available,
      categoryIds: profile.categories.map(c => c.id)
    };

    this.artisanService.updateProfile(request).subscribe({
      next: (res) => {
        if (this.artisanProfile()) {
          this.artisanProfile.set(res.data);
        }
      }
    });
  }

  navigateToEditProfile() {
    this.router.navigate(['/artisan/edit-profile']);
  }

  get artisanName(): string {
    const user = this.authService.currentUser();
    return user ? `${user.firstName} ${user.lastName}` : '';
  }
}
