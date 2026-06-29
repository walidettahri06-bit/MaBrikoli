import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import {
  LucideSparkles,
  LucideMail,
  LucidePhone,
  LucideMapPin,
  LucideArrowUpRight
} from '@lucide/angular';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [
    LucideSparkles,
    LucideMail,
    LucidePhone,
    LucideMapPin,
    LucideArrowUpRight
  ],
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss']
})
export class FooterComponent {
  router = inject(Router);

  navigateTo(path: string) {
    this.router.navigate([`/${path}`]);
  }
}
