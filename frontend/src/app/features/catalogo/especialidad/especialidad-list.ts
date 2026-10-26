import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { Especialidad } from './especialidad.model';
import { EspecialidadService } from './especialidad.service';

@Component({
  selector: 'app-especialidad-list',
  imports: [RouterLink],
  templateUrl: './especialidad-list.html',
})
export class EspecialidadList implements OnInit {
  private readonly especialidadService = inject(EspecialidadService);
  protected readonly auth = inject(AuthService);

  protected readonly especialidades = signal<Especialidad[]>([]);
  protected readonly error = signal<string | null>(null);
  protected readonly loading = signal(false);
  protected readonly deletingId = signal<number | null>(null);

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.loading.set(true);
    this.error.set(null);
    this.especialidadService.listar().subscribe({
      next: (data) => this.especialidades.set(data),
      error: () => {
        this.error.set('No se pudo cargar el catálogo. Verifica que el backend esté activo.');
        this.loading.set(false);
      },
      complete: () => this.loading.set(false),
    });
  }

  eliminar(especialidad: Especialidad): void {
    if (especialidad.id == null) return;
    if (!confirm(`¿Deseas eliminar la especialidad "${especialidad.nombre}"?`)) return;

    this.deletingId.set(especialidad.id);
    this.error.set(null);
    this.especialidadService.eliminar(especialidad.id).subscribe({
      next: () => {
        this.deletingId.set(null);
        this.cargar();
      },
      error: (response: HttpErrorResponse) => {
        this.error.set(
          response.status === 409 || response.status === 500
            ? 'No se puede eliminar: comprueba que no tenga procedimientos asociados.'
            : 'No se pudo eliminar la especialidad. Inténtalo nuevamente.',
        );
        this.deletingId.set(null);
      },
    });
  }
}
