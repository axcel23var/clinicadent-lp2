export const environment = {
  apiBaseUrl: 'http://localhost:8080',
  keycloak: {
    url: 'http://localhost:38081',
    realm: 'clinicadent',
    clientId: 'clinicadent-frontend',
    callbackUrl: 'http://localhost:4200/auth/callback',
    silentCheckSsoUrl: 'http://localhost:4200/assets/silent-check-sso.html',
  },
};
