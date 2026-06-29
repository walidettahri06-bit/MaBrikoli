import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ApplicationService } from '../../../core/services/application.service';
import { ArtisanService } from '../../../core/services/artisan.service';
import { BookingService } from '../../../core/services/booking.service';
import { CategoryService } from '../../../core/services/category.service';
import { ArtisanApplicationResponse } from '../../../core/models/application.model';
import { ArtisanProfileResponse } from '../../../core/models/artisan.model';
import { BookingResponse } from '../../../core/models/booking.model';
import { Category } from '../../../core/models/category.model';
import {
  LucideFolderCheck,
  LucideShieldCheck,
  LucideUsers,
  LucideCalendar,
  LucidePlus,
  LucideTrash2,
  LucideCheck
} from '@lucide/angular';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    LucideFolderCheck,
    LucideShieldCheck,
    LucideUsers,
    LucideCalendar,
    LucidePlus,
    LucideTrash2,
    LucideCheck
  ],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.scss']
})
export class AdminDashboardComponent implements OnInit {
  applicationService = inject(ApplicationService);
  artisanService = inject(ArtisanService);
  bookingService = inject(BookingService);
  categoryService = inject(CategoryService);
  router = inject(Router);

  applications = signal<ArtisanApplicationResponse[]>([]);
  artisans = signal<ArtisanProfileResponse[]>([]);
  bookings = signal<BookingResponse[]>([]);
  categories = signal<Category[]>([]);

  newCatName = '';
  newCatDesc = '';
  successMsg = '';
  error = '';

  pendingApplications = computed(() => {
    return this.applications().filter(app => app.status === 'PENDING');
  });

  approvedCount = computed(() => {
    return this.artisans().filter(art => art.verified).length;
  });

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.applicationService.getAllApplications().subscribe({
      next: (list) => this.applications.set(resolveData(list)),
      error: (err) => console.error('Failed to load applications:', err)
    });

    this.artisanService.getArtisans().subscribe({
      next: (list) => this.artisans.set(list),
      error: (err) => console.error('Failed to load artisans:', err)
    });

    this.bookingService.getAllBookings().subscribe({
      next: (list) => this.bookings.set(list),
      error: (err) => console.error('Failed to load bookings:', err)
    });

    this.loadCategories();
  }

  loadCategories() {
    this.categoryService.getCategories().subscribe({
      next: (list) => this.categories.set(list)
    });
  }

  handleAddCategory(e: Event) {
    e.preventDefault();
    if (!this.newCatName || !this.newCatDesc) return;

    this.categoryService.createCategory(this.newCatName, this.newCatDesc).subscribe({
      next: () => {
        this.newCatName = '';
        this.newCatDesc = '';
        this.successMsg = 'Trade category created successfully!';
        this.loadCategories();
        setTimeout(() => this.successMsg = '', 3000);
      },
      error: (err) => {
        this.error = 'Failed to create category.';
      }
    });
  }

  handleDeleteCategory(id: number) {
    if (!confirm('Are you sure you want to delete this category?')) return;
    this.categoryService.deleteCategory(id).subscribe({
      next: () => {
        this.loadCategories();
      },
      error: (err) => {
        alert(err.error?.message || 'Failed to delete category. It might be linked to existing profiles.');
      }
    });
  }

  reviewApplication(id: number) {
    this.router.navigate([`/admin/application-details`, id]);
  }
}

// Helper to handle both unwrapped data arrays or wrapped responses safely
function resolveData(response: any): any[] {
  if (Array.isArray(response)) return response;
  if (response && Array.isArray(response.data)) return response.data;
  return [];
}
