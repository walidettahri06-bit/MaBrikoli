import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const roleGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const expectedRoles = route.data['roles'] as string[];
  const user = authService.currentUser();

  if (user && expectedRoles && expectedRoles.includes(user.role)) {
    return true;
  }

  // Unauthorized — redirect to dashboard or home based on role
  if (authService.isLoggedIn()) {
    router.navigate(['/']);
  } else {
    router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
  }
  return false;
};
