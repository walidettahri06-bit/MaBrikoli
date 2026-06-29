import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./features/public/landing-page/landing-page.component').then(m => m.LandingPageComponent)
  },
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'register/client',
    loadComponent: () => import('./features/auth/register-client/register-client.component').then(m => m.RegisterClientComponent)
  },
  {
    path: 'register/artisan',
    loadComponent: () => import('./features/auth/register-artisan/register-artisan.component').then(m => m.RegisterArtisanComponent)
  },
  {
    path: 'forgot-password',
    loadComponent: () => import('./features/auth/forgot-password/forgot-password.component').then(m => m.ForgotPasswordComponent)
  },
  {
    path: 'client/search',
    loadComponent: () => import('./features/client/client-search/client-search.component').then(m => m.ClientSearchComponent)
  },
  {
    path: 'client/artisan-details/:id',
    loadComponent: () => import('./features/client/client-artisan-details/client-artisan-details.component').then(m => m.ClientArtisanDetailsComponent)
  },
  {
    path: 'client/dashboard',
    loadComponent: () => import('./features/client/client-dashboard/client-dashboard.component').then(m => m.ClientDashboardComponent),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['Client'] }
  },
  {
    path: 'client/booking-form/:id',
    loadComponent: () => import('./features/client/client-booking-views/client-booking-form.component').then(m => m.ClientBookingFormComponent),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['Client'] }
  },
  {
    path: 'client/bookings',
    loadComponent: () => import('./features/client/client-booking-views/client-booking-history.component').then(m => m.ClientBookingHistoryComponent),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['Client'] }
  },
  {
    path: 'client/favorites',
    loadComponent: () => import('./features/client/client-dashboard/client-favorites.component').then(m => m.ClientFavoritesComponent),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['Client'] }
  },
  {
    path: 'client/settings',
    loadComponent: () => import('./features/client/client-dashboard/client-settings.component').then(m => m.ClientSettingsComponent),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['Client'] }
  },
  {
    path: 'artisan/dashboard',
    loadComponent: () => import('./features/artisan/artisan-views/artisan-dashboard.component').then(m => m.ArtisanDashboardComponent),
    canActivate: [authGuard]
  },
  {
    path: 'artisan/edit-profile',
    loadComponent: () => import('./features/artisan/artisan-views/artisan-edit-profile.component').then(m => m.ArtisanEditProfileComponent),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['Artisan'] }
  },
  {
    path: 'admin/dashboard',
    loadComponent: () => import('./features/admin/admin-views/admin-dashboard.component').then(m => m.AdminDashboardComponent),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['Admin'] }
  },
  {
    path: 'admin/application-details/:id',
    loadComponent: () => import('./features/admin/admin-views/admin-application-details.component').then(m => m.AdminApplicationDetailsComponent),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['Admin'] }
  },
  {
    path: '**',
    redirectTo: ''
  }
];
