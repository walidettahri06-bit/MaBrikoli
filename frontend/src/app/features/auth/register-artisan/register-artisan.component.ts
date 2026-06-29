import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { CategoryService } from '../../../core/services/category.service';
import { ApplicationService } from '../../../core/services/application.service';
import { Category } from '../../../core/models/category.model';
import {
  LucideMail,
  LucideLock,
  LucideUser,
  LucidePhone,
  LucideMapPin,
  LucideShieldAlert,
  LucideShieldCheck,
  LucideFileUp,
  LucideArrowLeft
} from '@lucide/angular';

@Component({
  selector: 'app-register-artisan',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    LucideMail,
    LucideLock,
    LucideUser,
    LucidePhone,
    LucideMapPin,
    LucideShieldAlert,
    LucideShieldCheck,
    LucideFileUp,
    LucideArrowLeft
  ],
  templateUrl: './register-artisan.component.html',
  styleUrls: ['./register-artisan.component.scss']
})
export class RegisterArtisanComponent implements OnInit {
  authService = inject(AuthService);
  categoryService = inject(CategoryService);
  applicationService = inject(ApplicationService);
  router = inject(Router);

  firstName = '';
  lastName = '';
  email = '';
  phoneNumber = '';
  password = '';
  city = 'Casablanca';
  categoryId: number | null = null;
  yearsOfExperience = 3;
  description = '';
  error = '';

  categories: Category[] = [];
  docs: { name: string; size: string }[] = [];

  ngOnInit() {
    this.categoryService.getCategories().subscribe({
      next: (categories) => {
        this.categories = categories;
        if (this.categories.length > 0) {
          this.categoryId = this.categories[0].id;
        }
      },
      error: (err) => {
        console.error('Failed to load categories:', err);
      }
    });
  }

  handleDocUploadSimulate(e: Event) {
    const input = e.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      const file = input.files[0];
      this.docs.push({
        name: file.name,
        size: (file.size / (1024 * 1024)).toFixed(1) + ' MB'
      });
    }
  }

  onSubmit() {
    if (!this.firstName || !this.lastName || !this.email || !this.phoneNumber || !this.password || !this.categoryId || !this.description) {
      this.error = 'All fields marked with * are required.';
      return;
    }
    if (this.password.length < 8) {
      this.error = 'Password must be at least 8 characters long.';
      return;
    }
    this.error = '';

    // Step 1: Register user account
    this.authService.registerClient(
      this.firstName,
      this.lastName,
      this.email,
      this.phoneNumber,
      this.password
    ).subscribe({
      next: () => {
        // Step 2: Submit vetting application
        const applicationRequest = {
          firstName: this.firstName,
          lastName: this.lastName,
          phoneNumber: this.phoneNumber,
          city: this.city,
          yearsOfExperience: this.yearsOfExperience,
          description: this.description,
          categoryId: this.categoryId!,
          diplomaFileUrl: 'https://mabricoli.ma/files/diploma_placeholder.pdf',
          nationalIdFileUrl: 'https://mabricoli.ma/files/nid_placeholder.pdf',
          personalPhotoUrl: `https://api.dicebear.com/7.x/pixel-art/svg?seed=${this.firstName}`
        };

        this.applicationService.submitApplication(applicationRequest).subscribe({
          next: () => {
            // Logged in as Client, application pending. Redirect to homepage or profile/application dashboard
            this.router.navigate(['/']);
          },
          error: (err) => {
            this.error = 'Account created, but application submission failed: ' + (err.error?.message || 'Unknown error');
          }
        });
      },
      error: (err) => {
        this.error = err.error?.message || 'Registration failed. Email or phone number might already exist.';
      }
    });
  }

  navigateToLogin() {
    this.router.navigate(['/login']);
  }
}
