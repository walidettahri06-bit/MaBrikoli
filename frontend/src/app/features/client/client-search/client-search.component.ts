import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { ArtisanService } from '../../../core/services/artisan.service';
import { CategoryService } from '../../../core/services/category.service';
import { ArtisanProfileResponse } from '../../../core/models/artisan.model';
import { Category } from '../../../core/models/category.model';
import { ArtisanCardComponent } from '../../../shared/components/artisan-card/artisan-card.component';
import {
  LucideSearch,
  LucideMapPin,
  LucideSlidersHorizontal,
  LucideStar,
  LucideSparkles
} from '@lucide/angular';

@Component({
  selector: 'app-client-search',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ArtisanCardComponent,
    LucideSearch,
    LucideMapPin,
    LucideSlidersHorizontal,
    LucideStar,
    LucideSparkles
  ],
  templateUrl: './client-search.component.html',
  styleUrls: ['./client-search.component.scss']
})
export class ClientSearchComponent implements OnInit {
  artisanService = inject(ArtisanService);
  categoryService = inject(CategoryService);
  route = inject(ActivatedRoute);

  artisans = signal<ArtisanProfileResponse[]>([]);
  categories = signal<Category[]>([]);

  searchQuery = '';
  selectedCategory = '';
  selectedCity = '';
  maxPrice = 500;
  minRating = 0;
  onlyAvailable = false;
  showFiltersMobile = false;

  ngOnInit() {
    // 1. Fetch categories
    this.categoryService.getCategories().subscribe({
      next: (categories) => this.categories.set(categories)
    });

    // 2. Fetch artisans
    this.artisanService.getArtisans().subscribe({
      next: (list) => this.artisans.set(list)
    });

    // 3. Read query parameters
    this.route.queryParams.subscribe(params => {
      if (params['query']) this.searchQuery = params['query'];
      if (params['category']) this.selectedCategory = params['category'];
      if (params['city']) this.selectedCity = params['city'];
    });
  }

  get filteredArtisans(): ArtisanProfileResponse[] {
    return this.artisans().filter(art => {
      // Must be verified
      if (!art.verified) return false;

      const nameMatch = `${art.firstName} ${art.lastName}`.toLowerCase();
      const bioMatch = (art.bio || '').toLowerCase();
      const query = this.searchQuery.toLowerCase();

      const matchesSearch = !this.searchQuery ||
        nameMatch.includes(query) ||
        bioMatch.includes(query) ||
        art.categories.some(c => c.name.toLowerCase().includes(query));

      const matchesCategory = !this.selectedCategory ||
        art.categories.some(c => c.name.toLowerCase() === this.selectedCategory.toLowerCase());

      const matchesCity = !this.selectedCity ||
        (art.city || '').toLowerCase() === this.selectedCity.toLowerCase();

      const matchesPrice = !art.hourlyPrice || art.hourlyPrice <= this.maxPrice;
      const matchesRating = art.averageRating >= this.minRating;
      const matchesAvailability = !this.onlyAvailable || art.available;

      return matchesSearch && matchesCategory && matchesCity && matchesPrice && matchesRating && matchesAvailability;
    });
  }

  clearFilters() {
    this.searchQuery = '';
    this.selectedCategory = '';
    this.selectedCity = '';
    this.maxPrice = 500;
    this.minRating = 0;
    this.onlyAvailable = false;
  }

  toggleOnlyAvailable() {
    this.onlyAvailable = !this.onlyAvailable;
  }

  toggleFiltersMobile() {
    this.showFiltersMobile = !this.showFiltersMobile;
  }
}
