import { Component, input, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { ArtisanProfileResponse } from '../../../core/models/artisan.model';
import {
  LucideStar,
  LucideMapPin,
  LucideAward,
  LucideHeart,
  LucideShieldCheck,
  LucideClock,
  LucideUserCheck
} from '@lucide/angular';

@Component({
  selector: 'app-artisan-card',
  standalone: true,
  imports: [
    CommonModule,
    LucideStar,
    LucideMapPin,
    LucideAward,
    LucideHeart,
    LucideShieldCheck,
    LucideClock,
    LucideUserCheck
  ],
  templateUrl: './artisan-card.component.html',
  styleUrls: ['./artisan-card.component.scss']
})
export class ArtisanCardComponent {
  artisan = input.required<ArtisanProfileResponse>();

  router = inject(Router);
  authService = inject(AuthService);

  isFav = signal(false);

  get artisanName(): string {
    return `${this.artisan().firstName} ${this.artisan().lastName}`;
  }

  get artisanAvatar(): string {
    return this.artisan().profilePhoto || `https://api.dicebear.com/7.x/pixel-art/svg?seed=${this.artisan().firstName}`;
  }

  get profession(): string {
    const cats = this.artisan().categories;
    return cats && cats.length > 0 ? cats[0].name : 'Artisan';
  }

  get skills(): string[] {
    const cats = this.artisan().categories;
    return cats ? cats.map(c => c.name) : [];
  }

  get availabilityText(): string {
    return this.artisan().available ? 'Available' : 'Busy';
  }

  getAvailabilityStyle(): string {
    return this.artisan().available
      ? 'bg-emerald-50 text-emerald-700 border-emerald-100'
      : 'bg-rose-50 text-rose-700 border-rose-100';
  }

  handleFavoriteClick(e: MouseEvent) {
    e.stopPropagation();
    this.isFav.update(v => !v);
  }

  handleViewProfile() {
    this.router.navigate([`/client/artisan-details`, this.artisan().id]);
  }

  handleBookNow(e: MouseEvent) {
    e.stopPropagation();
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
    } else {
      this.router.navigate([`/client/booking-form`, this.artisan().id]);
    }
  }
}
