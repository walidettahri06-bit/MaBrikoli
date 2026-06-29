import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ArtisanService } from '../../../core/services/artisan.service';
import { ArtisanProfileResponse } from '../../../core/models/artisan.model';
import { LucideCheckCircle } from '@lucide/angular';

@Component({
  selector: 'app-artisan-edit-profile',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    LucideCheckCircle
  ],
  templateUrl: './artisan-edit-profile.component.html',
  styleUrls: ['./artisan-edit-profile.component.scss']
})
export class ArtisanEditProfileComponent implements OnInit {
  artisanService = inject(ArtisanService);
  router = inject(Router);

  artisan = signal<ArtisanProfileResponse | null>(null);
  firstName = '';
  lastName = '';
  bio = '';
  yearsOfExperience = 0;
  city = 'Casablanca';
  address = '';
  hourlyPrice = 150;
  available = true;
  saved = false;
  error = '';

  ngOnInit() {
    this.artisanService.getMyProfile().subscribe({
      next: (profile) => {
        this.artisan.set(profile);
        this.firstName = profile.firstName;
        this.lastName = profile.lastName;
        this.bio = profile.bio || '';
        this.yearsOfExperience = profile.yearsOfExperience;
        this.city = profile.city || 'Casablanca';
        this.address = profile.address || '';
        this.hourlyPrice = profile.hourlyPrice || 150;
        this.available = profile.available;
      },
      error: (err) => {
        this.error = 'Failed to load profile details.';
      }
    });
  }

  onSubmit() {
    const current = this.artisan();
    if (!current) return;

    if (!this.bio || !this.city || this.hourlyPrice === null) {
      this.error = 'Please fill out all required fields.';
      return;
    }

    this.error = '';
    const request = {
      bio: this.bio,
      yearsOfExperience: this.yearsOfExperience,
      city: this.city,
      address: this.address,
      hourlyPrice: this.hourlyPrice,
      available: this.available,
      categoryIds: current.categories ? current.categories.map(c => c.id) : []
    };

    this.artisanService.updateProfile(request).subscribe({
      next: () => {
        this.saved = true;
        setTimeout(() => {
          this.saved = false;
          this.router.navigate(['/artisan/dashboard']);
        }, 1500);
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to update profile settings.';
      }
    });
  }

  cancel() {
    this.router.navigate(['/artisan/dashboard']);
  }
}
