import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute } from '@angular/router';
import { ArtisanService } from '../../../core/services/artisan.service';
import { ReviewService } from '../../../core/services/review.service';
import { AuthService } from '../../../core/services/auth.service';
import { ArtisanProfileResponse } from '../../../core/models/artisan.model';
import { ReviewResponse } from '../../../core/models/review.model';
import {
  LucideStar,
  LucideMapPin,
  LucidePhone,
  LucideAward,
  LucideShieldCheck,
  LucideHeart,
  LucideArrowLeft,
  LucideCheckCircle,
  LucideFileText,
  LucideCalendar,
  LucideShieldAlert
} from '@lucide/angular';

@Component({
  selector: 'app-client-artisan-details',
  standalone: true,
  imports: [
    CommonModule,
    LucideStar,
    LucideMapPin,
    LucidePhone,
    LucideAward,
    LucideShieldCheck,
    LucideHeart,
    LucideArrowLeft,
    LucideCheckCircle,
    LucideFileText,
    LucideCalendar,
    LucideShieldAlert
  ],
  templateUrl: './client-artisan-details.component.html',
  styleUrls: ['./client-artisan-details.component.scss']
})
export class ClientArtisanDetailsComponent implements OnInit {
  artisanService = inject(ArtisanService);
  reviewService = inject(ReviewService);
  authService = inject(AuthService);
  router = inject(Router);
  route = inject(ActivatedRoute);

  artisan = signal<ArtisanProfileResponse | null>(null);
  reviews = signal<ReviewResponse[]>([]);
  isFav = signal(false);
  error = signal('');

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const idStr = params.get('id');
      if (idStr) {
        const id = Number(idStr);
        this.loadArtisan(id);
        this.checkIfFavorite(id);
      }
    });
  }

  loadArtisan(id: number) {
    this.artisanService.getArtisanById(id).subscribe({
      next: (data) => {
        this.artisan.set(data);
        this.loadReviews(id);
      },
      error: (err) => {
        this.error.set('Artisan profile not found.');
      }
    });
  }

  loadReviews(artisanId: number) {
    this.reviewService.getReviewsForArtisan(artisanId).subscribe({
      next: (list) => this.reviews.set(list)
    });
  }

  checkIfFavorite(id: number) {
    const favsJson = localStorage.getItem('favorites');
    if (favsJson) {
      try {
        const favs = JSON.parse(favsJson) as number[];
        this.isFav.set(favs.includes(id));
      } catch (e) {
        this.isFav.set(false);
      }
    }
  }

  toggleFavorite() {
    const currentArtisan = this.artisan();
    if (!currentArtisan) return;

    const favsJson = localStorage.getItem('favorites');
    let favs: number[] = [];
    if (favsJson) {
      try {
        favs = JSON.parse(favsJson) as number[];
      } catch (e) {
        favs = [];
      }
    }

    if (favs.includes(currentArtisan.id)) {
      favs = favs.filter(fid => fid !== currentArtisan.id);
      this.isFav.set(false);
    } else {
      favs.push(currentArtisan.id);
      this.isFav.set(true);
    }
    localStorage.setItem('favorites', JSON.stringify(favs));
  }

  handleBookClick() {
    const currentArtisan = this.artisan();
    if (!currentArtisan) return;

    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
    } else {
      this.router.navigate([`/client/booking-form`, currentArtisan.id]);
    }
  }

  navigateToDirectory() {
    this.router.navigate(['/client/search']);
  }

  get artisanName(): string {
    const a = this.artisan();
    return a ? `${a.firstName} ${a.lastName}` : '';
  }

  get artisanAvatar(): string {
    const a = this.artisan();
    return a ? (a.profilePhoto || `https://api.dicebear.com/7.x/pixel-art/svg?seed=${a.firstName}`) : '';
  }

  get profession(): string {
    const a = this.artisan();
    return a && a.categories && a.categories.length > 0 ? a.categories[0].name : 'Artisan';
  }

  get skills(): string[] {
    const a = this.artisan();
    return a && a.categories ? a.categories.map(c => c.name) : [];
  }
}
