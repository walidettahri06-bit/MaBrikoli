import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CategoryService } from '../../../core/services/category.service';
import { ArtisanService } from '../../../core/services/artisan.service';
import { Category } from '../../../core/models/category.model';
import { ArtisanProfileResponse } from '../../../core/models/artisan.model';
import { AccordionComponent, AccordionItem } from '../../../shared/components/accordion/accordion.component';
import { ArtisanCardComponent } from '../../../shared/components/artisan-card/artisan-card.component';
import {
  LucideSearch,
  LucideMapPin,
  LucideSparkles,
  LucideArrowRight,
  LucideStar,
  LucideShieldCheck,
  LucideDroplet,
  LucideZap,
  LucideHammer,
  LucidePaintbrush,
  LucideWrench,
  LucideFlower2,
  LucideKey
} from '@lucide/angular';

@Component({
  selector: 'app-landing-page',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    AccordionComponent,
    ArtisanCardComponent,
    LucideSearch,
    LucideMapPin,
    LucideSparkles,
    LucideArrowRight,
    LucideStar,
    LucideShieldCheck,
    LucideDroplet,
    LucideZap,
    LucideHammer,
    LucidePaintbrush,
    LucideWrench,
    LucideFlower2,
    LucideKey
  ],
  templateUrl: './landing-page.component.html',
  styleUrls: ['./landing-page.component.scss']
})
export class LandingPageComponent implements OnInit {
  categoryService = inject(CategoryService);
  artisanService = inject(ArtisanService);
  router = inject(Router);

  // Live data signals
  categories = signal<Category[]>([]);
  artisans = signal<ArtisanProfileResponse[]>([]);

  // Search input signals
  searchQuery = signal('');
  cityQuery = signal('');

  // Derived featured artisans
  featuredArtisans = computed(() => this.artisans().slice(0, 3));

  faqItems: AccordionItem[] = [
    {
      title: "How do you screen and vet artisans?",
      content: "Every artisan submitting an application to Mabrikoli must upload structural identity documents, national registry codes, and accredited professional training or guilds certification. Our administration team manually reviews every file and performs reference background checks on recent projects before giving them the 'Verified' badge."
    },
    {
      title: "How does the payment process work?",
      content: "Mabrikoli lists artisan hourly rates transparently. Clients submit booking requests detailing duration. Once confirmed and completed, clients pay artisans directly as per agreed rates or through custom invoices. There are zero hidden fees."
    },
    {
      title: "What happens if there's damage or poor service?",
      content: "All Mabrikoli Verified artisans are insured and pledge to our Quality Seal. In the rare case of issues, our responsive customer support acts as a mediator to inspect complaints and resolve damages promptly."
    },
    {
      title: "Can I join as an artisan if I'm independent?",
      content: "Yes, independent professional artisans, contractors, and specialized companies are welcome. Simply click 'Become an Artisan', submit your identity cards, certificates of insurance or professional diplomas, and you will go live once verified by our admin board."
    }
  ];

  ngOnInit() {
    this.loadCategories();
    this.loadArtisans();
  }

  loadCategories() {
    this.categoryService.getCategories().subscribe({
      next: (data) => this.categories.set(data),
      error: (err) => console.error('Failed to load categories:', err)
    });
  }

  loadArtisans() {
    this.artisanService.getArtisans().subscribe({
      next: (data) => this.artisans.set(data),
      error: (err) => console.error('Failed to load artisans:', err)
    });
  }

  handleSearchSubmit(e: Event) {
    e.preventDefault();
    this.router.navigate(['/client/search'], {
      queryParams: {
        query: this.searchQuery(),
        city: this.cityQuery()
      }
    });
  }

  navigateToCategory(categoryName: string) {
    this.router.navigate(['/client/search'], {
      queryParams: { category: categoryName }
    });
  }

  navigateToSearch() {
    this.router.navigate(['/client/search']);
  }
}
