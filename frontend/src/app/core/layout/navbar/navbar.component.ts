import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import {
  LucideSparkles,
  LucideBell,
  LucideMenu,
  LucideX,
  LucideUser,
  LucideLogOut,
  LucideChevronDown,
  LucideHeart,
  LucideCalendar,
  LucideSettings,
  LucideShieldCheck,
  LucideHelpCircle,
  LucideInfo,
  LucideLayoutDashboard
} from '@lucide/angular';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    CommonModule,
    LucideSparkles,
    LucideBell,
    LucideMenu,
    LucideX,
    LucideUser,
    LucideLogOut,
    LucideChevronDown,
    LucideHeart,
    LucideCalendar,
    LucideSettings,
    LucideShieldCheck,
    LucideHelpCircle,
    LucideInfo,
    LucideLayoutDashboard
  ],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent {
  authService = inject(AuthService);
  router = inject(Router);

  // States using signals
  showNotifications = signal(false);
  showProfileMenu = signal(false);
  mobileMenuOpen = signal(false);

  // Mock notifications matching React dashboard structure
  notifications = [
    { id: 1, role: 'Client', title: 'Booking Confirmed', description: 'Your booking with Karim has been approved.', date: '10m ago', read: false },
    { id: 2, role: 'Artisan', title: 'New Request', description: 'You have a new plumbing request in Casablanca.', date: '30m ago', read: false }
  ];

  get activeRole() {
    return this.authService.currentRole();
  }

  get currentUser() {
    return this.authService.currentUser();
  }

  get unreadNotifications() {
    return this.notifications.filter(n => n.role === this.activeRole && !n.read);
  }

  get currentViewPath() {
    return this.router.url;
  }

  handleNotificationClick() {
    this.showNotifications.update(v => !v);
    if (this.showNotifications()) {
      this.notifications.forEach(n => {
        if (n.role === this.activeRole) n.read = true;
      });
    }
  }

  navigateTo(path: string) {
    this.mobileMenuOpen.set(false);
    this.router.navigate([`/${path}`]);
  }

  getDashboardView(): string {
    const role = this.activeRole;
    if (role === 'Client') return 'client/dashboard';
    if (role === 'Artisan') return 'artisan/dashboard';
    if (role === 'Admin') return 'admin/dashboard';
    return '';
  }
}
