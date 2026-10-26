import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { forkJoin, of } from 'rxjs';
import { Atencion, Cita, ClinicApiService, Paciente, Pago, Tratamiento } from '../services/clinic-api.service';
import { AuthService } from '../auth/auth.service';

@Component({
  selector: 'app-inicio',
  imports: [RouterLink, DatePipe, CurrencyPipe],
  templateUrl: './inicio.html',
})
export class Inicio implements OnInit {
  private readonly api = inject(ClinicApiService);
  private readonly auth = inject(AuthService);

  protected readonly citas = signal<Cita[]>([]);
  protected readonly pacientes = signal<Paciente[]>([]);
  protected readonly tratamientos = signal<Tratamiento[]>([]);
  protected readonly atenciones = signal<Atencion[]>([]);
  protected readonly pagos = signal<Pago[]>([]);
  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);

  ngOnInit(): void {
    forkJoin({
      citas: this.canViewAdmission() ? this.api.citas() : of([]),
      pacientes: this.canViewAdmission() ? this.api.pacientes() : of([]),
      tratamientos: this.canViewTreatments() ? this.api.tratamientos() : of([]),
      atenciones: this.canViewAttentions() ? this.api.atenciones() : of([]),
      pagos: this.canViewPayments() ? this.api.pagos() : of([]),
    }).subscribe({
      next: ({ citas, pacientes, tratamientos, atenciones, pagos }) => {
        this.citas.set(citas);
        this.pacientes.set(pacientes);
        this.tratamientos.set(tratamientos);
        this.atenciones.set(atenciones);
        this.pagos.set(pagos);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('No se pudieron cargar los datos de la clínica. Actualiza el panel para volver a intentar.');
        this.loading.set(false);
      },
    });
  }

  citasDeHoy(): Cita[] {
    const now = new Date();
    return this.citas()
      .filter((cita) => {
        const fecha = new Date(cita.fechaHora);
        return fecha.getFullYear() === now.getFullYear()
          && fecha.getMonth() === now.getMonth()
          && fecha.getDate() === now.getDate()
          && cita.estado !== 'CANCELADA';
      })
      .sort((a, b) => a.fechaHora.localeCompare(b.fechaHora));
  }

  planesActivos(): number {
    return this.tratamientos().filter((tratamiento) => tratamiento.estado !== 'FINALIZADO').length;
  }

  saldoPorCobrar(): number {
    return this.tratamientos().reduce((total, tratamiento) => total + tratamiento.saldoPendiente, 0);
  }

  nombrePaciente(id: number): string {
    const paciente = this.pacientes().find((item) => item.id === id);
    return paciente ? `${paciente.apellidos}, ${paciente.nombres}` : `Paciente #${id}`;
  }

  fechaActual(): string {
    return new Intl.DateTimeFormat('es-PE', {
      weekday: 'long',
      day: 'numeric',
      month: 'long',
      year: 'numeric',
    }).format(new Date());
  }

  canViewAdmission(): boolean {
    return this.auth.hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA');
  }

  canViewTreatments(): boolean {
    return this.auth.hasAnyRole('ADMINISTRADOR', 'ODONTOLOGO');
  }

  canViewAttentions(): boolean {
    return this.auth.hasAnyRole('ADMINISTRADOR', 'ODONTOLOGO');
  }

  canViewPayments(): boolean {
    return this.auth.hasAnyRole('ADMINISTRADOR', 'CAJERO');
  }

  canViewCatalogs(): boolean {
    return this.auth.hasAnyRole('ADMINISTRADOR', 'ODONTOLOGO', 'RECEPCIONISTA', 'CAJERO');
  }
}
