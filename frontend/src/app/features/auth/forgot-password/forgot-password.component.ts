import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import {
  LucideArrowLeft,
  LucideCheckCircle,
  LucideMail
} from '@lucide/angular';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    LucideArrowLeft,
    LucideCheckCircle,
    LucideMail
  ],
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss']
})
export class ForgotPasswordComponent {
  router = inject(Router);

  email = '';
  done = false;

  onSubmit() {
    if (this.email) {
      this.done = true;
    }
  }

  navigateToLogin() {
    this.router.navigate(['/login']);
  }
}
