import { Injectable, signal, computed, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';

export interface UserSession {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  name: string;
  role: 'Client' | 'Artisan' | 'Admin' | 'Public';
  avatar: string;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  userId: number;
  email: string;
  firstName: string;
  lastName: string;
  role: string; // e.g. "ROLE_CLIENT", "ROLE_ARTISAN", "ROLE_ADMIN"
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private apiUrl = `${environment.apiUrl}/auth`;

  // Signals for state management
  private currentUserSignal = signal<UserSession | null>(this.loadUserFromStorage());

  readonly currentUser = this.currentUserSignal.asReadonly();
  readonly isLoggedIn = computed(() => this.currentUserSignal() !== null);
  readonly currentRole = computed(() => this.currentUserSignal()?.role || 'Public');

  /**
   * Logs in a user using real credentials.
   */
  login(email: string, password: string): Observable<ApiResponse<LoginResponse>> {
    return this.http.post<ApiResponse<LoginResponse>>(`${this.apiUrl}/login`, { email, password }).pipe(
      tap((res) => {
        const data = res.data;
        let mappedRole: 'Client' | 'Artisan' | 'Admin' | 'Public' = 'Public';

        if (data.role === 'ROLE_CLIENT' || data.role === 'CLIENT') mappedRole = 'Client';
        else if (data.role === 'ROLE_ARTISAN' || data.role === 'ARTISAN') mappedRole = 'Artisan';
        else if (data.role === 'ROLE_ADMIN' || data.role === 'ADMIN') mappedRole = 'Admin';

        const session: UserSession = {
          id: data.userId,
          email: data.email,
          firstName: data.firstName,
          lastName: data.lastName,
          name: `${data.firstName} ${data.lastName}`,
          role: mappedRole,
          avatar: `https://api.dicebear.com/7.x/pixel-art/svg?seed=${data.email}`
        };

        localStorage.setItem('token', data.accessToken);
        localStorage.setItem('currentUser', JSON.stringify(session));
        this.currentUserSignal.set(session);
      })
    );
  }

  /**
   * Registers a new client and logs them in.
   */
  registerClient(firstName: string, lastName: string, email: string, phoneNumber: string, password: string): Observable<ApiResponse<LoginResponse>> {
    const payload = { firstName, lastName, email, phoneNumber, password };
    return this.http.post<ApiResponse<LoginResponse>>(`${this.apiUrl}/register/client`, payload).pipe(
      tap((res) => {
        const data = res.data;
        const session: UserSession = {
          id: data.userId,
          email: data.email,
          firstName: data.firstName,
          lastName: data.lastName,
          name: `${data.firstName} ${data.lastName}`,
          role: 'Client',
          avatar: `https://api.dicebear.com/7.x/pixel-art/svg?seed=${data.email}`
        };

        localStorage.setItem('token', data.accessToken);
        localStorage.setItem('currentUser', JSON.stringify(session));
        this.currentUserSignal.set(session);
      })
    );
  }

  /**
   * Registers a new artisan and logs them in.
   */
  registerArtisan(firstName: string, lastName: string, email: string, phoneNumber: string, password: string): Observable<ApiResponse<LoginResponse>> {
    const payload = { firstName, lastName, email, phoneNumber, password };
    return this.http.post<ApiResponse<LoginResponse>>(`${this.apiUrl}/register/artisan`, payload).pipe(
      tap((res) => {
        const data = res.data;
        const session: UserSession = {
          id: data.userId,
          email: data.email,
          firstName: data.firstName,
          lastName: data.lastName,
          name: `${data.firstName} ${data.lastName}`,
          role: 'Artisan',
          avatar: `https://api.dicebear.com/7.x/pixel-art/svg?seed=${data.email}`
        };

        localStorage.setItem('token', data.accessToken);
        localStorage.setItem('currentUser', JSON.stringify(session));
        this.currentUserSignal.set(session);
      })
    );
  }

  /**
   * Dedicated method for the demo switcher that triggers real logins for seeded accounts.
   */
  switchRole(role: 'Client' | 'Artisan' | 'Admin' | 'Public'): void {
    if (role === 'Public') {
      this.logout();
      return;
    }

    let email = '';
    let password = 'password123';

    if (role === 'Client') {
      email = 'client1@mabrikoli.com';
    } else if (role === 'Artisan') {
      email = 'artisan1@mabrikoli.com';
    } else if (role === 'Admin') {
      email = 'mohamed@admin.com';
      password = 'admin123';
    }

    this.login(email, password).subscribe({
      next: () => {
        this.router.navigate(['/']);
      },
      error: (err) => console.error('Demo switcher login failed:', err)
    });
  }

  /**
   * Logs out the user and clears localStorage.
   */
  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('currentUser');
    this.currentUserSignal.set(null);
    this.router.navigate(['/']);
  }

  private loadUserFromStorage(): UserSession | null {
    const userJson = localStorage.getItem('currentUser');
    if (userJson) {
      try {
        return JSON.parse(userJson);
      } catch (e) {
        return null;
      }
    }
    return null;
  }
}
