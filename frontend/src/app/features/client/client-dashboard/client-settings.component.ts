import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { LucideCheckCircle } from '@lucide/angular';

@Component({
  selector: 'app-client-settings',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    LucideCheckCircle
  ],
  templateUrl: './client-settings.component.html',
  styleUrls: ['./client-settings.component.scss']
})
export class ClientSettingsComponent implements OnInit {
  authService = inject(AuthService);

  name = '';
  email = '';
  phone = '+212 677-123456';
  city = 'Casablanca';
  address = 'Appt 14, Rue Ghandi, Casablanca';
  saved = false;

  ngOnInit() {
    const user = this.authService.currentUser();
    if (user) {
      this.name = user.name || `${user.firstName} ${user.lastName}`;
      this.email = user.email;
    }
  }

  onSubmit() {
    const user = this.authService.currentUser();
    if (user) {
      const parts = this.name.split(' ');
      const updatedUser = {
        ...user,
        name: this.name,
        firstName: parts[0] || user.firstName,
        lastName: parts.slice(1).join(' ') || user.lastName,
        email: this.email
      };

      localStorage.setItem('currentUser', JSON.stringify(updatedUser));
      // Reload user signals
      (this.authService as any).currentUserSignal.set(updatedUser);

      this.saved = true;
      setTimeout(() => this.saved = false, 3000);
    }
  }
}
