import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ArtisanService } from '../../../core/services/artisan.service';
import { ArtisanProfileResponse } from '../../../core/models/artisan.model';
import { ArtisanCardComponent } from '../../../shared/components/artisan-card/artisan-card.component';
import { LucideHeart } from '@lucide/angular';

@Component({
  selector: 'app-client-favorites',
  standalone: true,
  imports: [
    CommonModule,
    ArtisanCardComponent,
    LucideHeart
  ],
  templateUrl: './client-favorites.component.html',
  styleUrls: ['./client-favorites.component.scss']
})
export class ClientFavoritesComponent implements OnInit {
  artisanService = inject(ArtisanService);
  router = inject(Router);

  favoriteArtisans = signal<ArtisanProfileResponse[]>([]);

  ngOnInit() {
    this.loadFavorites();
  }

  loadFavorites() {
    const favsJson = localStorage.getItem('favorites');
    if (favsJson) {
      try {
        const favIds = JSON.parse(favsJson) as number[];
        if (favIds.length > 0) {
          this.artisanService.getArtisans().subscribe({
            next: (list) => {
              const matched = list.filter(art => favIds.includes(art.id));
              this.favoriteArtisans.set(matched);
            }
          });
        }
      } catch (e) {
        this.favoriteArtisans.set([]);
      }
    }
  }

  navigateToSearch() {
    this.router.navigate(['/client/search']);
  }
}
