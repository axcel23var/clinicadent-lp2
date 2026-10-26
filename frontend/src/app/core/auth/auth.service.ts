import { Injectable, signal } from '@angular/core';
import Keycloak from 'keycloak-js';
import { environment } from '../../../environments/environment';

interface ClinicaDentClaims {
  name?: string;
  preferred_username?: string;
  realm_access?: { roles?: string[] };
}

const RETURN_URL_KEY = 'clinicadent.auth.returnUrl';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly keycloak = new Keycloak({
    url: environment.keycloak.url,
    realm: environment.keycloak.realm,
    clientId: environment.keycloak.clientId,
  });

  private readonly authenticatedState = signal(false);
  private readonly rolesState = signal<string[]>([]);
  private readonly usernameState = signal<string | null>(null);

  readonly authenticated = this.authenticatedState.asReadonly();
  readonly roles = this.rolesState.asReadonly();
  readonly username = this.usernameState.asReadonly();

  async initialize(): Promise<void> {
    this.keycloak.onAuthSuccess = () => this.updateIdentity();
    this.keycloak.onAuthRefreshSuccess = () => this.updateIdentity();
    this.keycloak.onAuthLogout = () => this.clearIdentity();

    try {
      await this.keycloak.init({
        onLoad: 'login-required',
        pkceMethod: 'S256',
        checkLoginIframe: false,
        silentCheckSsoRedirectUri: environment.keycloak.silentCheckSsoUrl,
      });
      this.updateIdentity();
    } catch (error) {
      this.clearIdentity();
      console.error('No se pudo conectar con Keycloak.', error);
    }
  }

  async login(returnUrl = '/'): Promise<void> {
    sessionStorage.setItem(RETURN_URL_KEY, returnUrl);
    await this.keycloak.login({ redirectUri: environment.keycloak.callbackUrl });
  }

  async logout(): Promise<void> {
    this.clearIdentity();
    await this.keycloak.logout({ redirectUri: window.location.origin });
  }

  async refreshToken(): Promise<boolean> {
    if (!this.keycloak.authenticated) {
      return false;
    }

    try {
      await this.keycloak.updateToken(-1);
      this.updateIdentity();
      return true;
    } catch {
      this.clearIdentity();
      return false;
    }
  }

  accessToken(): string | null {
    return this.keycloak.token ?? null;
  }

  hasAnyRole(...allowedRoles: string[]): boolean {
    return allowedRoles.some((role) => this.rolesState().includes(role));
  }

  takeReturnUrl(): string {
    const returnUrl = sessionStorage.getItem(RETURN_URL_KEY);
    sessionStorage.removeItem(RETURN_URL_KEY);
    return returnUrl?.startsWith('/') && !returnUrl.startsWith('//') ? returnUrl : '/';
  }

  private updateIdentity(): void {
    const claims = this.keycloak.tokenParsed as ClinicaDentClaims | undefined;
    this.authenticatedState.set(Boolean(this.keycloak.authenticated && this.keycloak.token));
    this.rolesState.set(claims?.realm_access?.roles ?? []);
    this.usernameState.set(claims?.name ?? claims?.preferred_username ?? null);
  }

  private clearIdentity(): void {
    this.authenticatedState.set(false);
    this.rolesState.set([]);
    this.usernameState.set(null);
  }
}
