import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import {
  LucideMail,
  LucideLock,
  LucideShieldAlert,
  LucideSparkles
} from '@lucide/angular';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    LucideMail,
    LucideLock,
    LucideShieldAlert,
    LucideSparkles
  ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  authService = inject(AuthService);
  router = inject(Router);
  route = inject(ActivatedRoute);

  email = '';
  password = '';
  error = '';

  onSubmit() {
    if (!this.email) {
      this.error = 'Please fill in your email address.';
      return;
    }
    if (!this.password) {
      this.error = 'Please fill in your password.';
      return;
    }
    this.error = '';
    this.submitLogin();
  }

  private submitLogin() {
    this.authService.login(this.email, this.password).subscribe({
      next: (res) => {
        // Redirect to dashboard or returnUrl
        const returnUrl = this.route.snapshot.queryParams['returnUrl'] || this.getDashboardRouteByRole();
        this.router.navigateByUrl(returnUrl);
      },
      error: (err) => {
        this.error = err.error?.message || 'Invalid email or password. Please try again.';
      }
    });
  }

  private getDashboardRouteByRole(): string {
    const user = this.authService.currentUser();
    if (!user) return '/';
    if (user.role === 'Admin') return '/admin/dashboard';
    if (user.role === 'Artisan') return '/artisan/dashboard';
    return '/';
  }

  navigateToRegister(role: 'client' | 'artisan') {
    this.router.navigate([`/register/${role}`]);
  }

  navigateToForgot() {
    this.router.navigate(['/forgot-password']);
  }
}
