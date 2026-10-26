import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';

@Component({
  selector: 'app-auth-callback',
  template: '<p>Completando inicio de sesión...</p>',
})
export class AuthCallback {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  constructor() {
    void this.router.navigateByUrl(this.auth.takeReturnUrl());
  }
}
