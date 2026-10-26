import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-access-denied',
  imports: [RouterLink],
  template: `
    <main>
      <h1>Acceso denegado</h1>
      <p>Tu cuenta no tiene el rol requerido para esta sección.</p>
      <a routerLink="/">Volver al inicio</a>
    </main>
  `,
})
export class AccessDenied {}
