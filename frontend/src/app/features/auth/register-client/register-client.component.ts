import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import {
  LucideMail,
  LucideLock,
  LucideUser,
  LucidePhone,
  LucideArrowLeft,
  LucideShieldAlert
} from '@lucide/angular';

@Component({
  selector: 'app-register-client',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    LucideMail,
    LucideLock,
    LucideUser,
    LucidePhone,
    LucideArrowLeft,
    LucideShieldAlert
  ],
  templateUrl: './register-client.component.html',
  styleUrls: ['./register-client.component.scss']
})
export class RegisterClientComponent {
  authService = inject(AuthService);
  router = inject(Router);

  firstName = '';
  lastName = '';
  email = '';
  phoneNumber = '';
  password = '';
  error = '';

  onSubmit() {
    if (!this.firstName || !this.lastName || !this.email || !this.phoneNumber || !this.password) {
      this.error = 'All fields marked with * are required.';
      return;
    }
    if (this.password.length < 8) {
      this.error = 'Password must be at least 8 characters long.';
      return;
    }
    this.error = '';

    this.authService.registerClient(
      this.firstName,
      this.lastName,
      this.email,
      this.phoneNumber,
      this.password
    ).subscribe({
      next: () => {
        this.router.navigate(['/']);
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
