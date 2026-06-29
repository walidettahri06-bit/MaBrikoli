import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ApplicationService } from '../../../core/services/application.service';
import { ArtisanApplicationResponse } from '../../../core/models/application.model';
import {
  LucideArrowLeft,
  LucideShieldCheck,
  LucideFileText,
  LucideXCircle,
  LucideAlertCircle
} from '@lucide/angular';

@Component({
  selector: 'app-admin-application-details',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    LucideArrowLeft,
    LucideShieldCheck,
    LucideFileText,
    LucideXCircle,
    LucideAlertCircle
  ],
  templateUrl: './admin-application-details.component.html',
  styleUrls: ['./admin-application-details.component.scss']
})
export class AdminApplicationDetailsComponent implements OnInit {
  applicationService = inject(ApplicationService);
  router = inject(Router);
  route = inject(ActivatedRoute);

  application = signal<ArtisanApplicationResponse | null>(null);

  vettedCheck1 = false;
  vettedCheck2 = false;
  vettedCheck3 = false;
  error = '';

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const idStr = params.get('id');
      if (idStr) {
        this.applicationService.getApplicationById(Number(idStr)).subscribe({
          next: (res) => this.application.set(res.data),
          error: (err) => this.error = 'Application details not found.'
        });
      }
    });
  }

  handleApprove() {
    const app = this.application();
    if (!app) return;

    this.applicationService.approveApplication(app.id).subscribe({
      next: () => {
        this.router.navigate(['/admin/dashboard']);
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to approve application.';
      }
    });
  }

  handleReject() {
    const app = this.application();
    if (!app) return;

    const reason = prompt('Please enter rejection reason:');
    if (reason === null) return;

    this.applicationService.rejectApplication(app.id, reason).subscribe({
      next: () => {
        this.router.navigate(['/admin/dashboard']);
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to reject application.';
      }
    });
  }

  backToQueue() {
    this.router.navigate(['/admin/dashboard']);
  }
}
