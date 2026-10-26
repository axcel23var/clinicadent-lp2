import { Component, OnInit, inject, signal } from '@angular/core';
import { DatePipe, DecimalPipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ClinicApiService, Cita, Paciente, Procedimiento } from '../../../core/services/clinic-api.service';

interface CitaDetalleDraft {
  idProcedimiento: number;
  nombreProcedimiento: string;
  observaciones: string;
}

@Component({
  selector: 'app-cita-page',
  imports: [DatePipe, DecimalPipe, ReactiveFormsModule],
  templateUrl: './cita-page.html',
  styleUrl: '../../../shared/transaction-workflow.css',
})
export class CitaPage implements OnInit {
  private readonly api = inject(ClinicApiService);
  private readonly fb = inject(FormBuilder);
  protected readonly items = signal<Cita[]>([]);
  protected readonly pacientes = signal<Paciente[]>([]);
  protected readonly procedimientos = signal<Procedimiento[]>([]);
  protected readonly detalles = signal<CitaDetalleDraft[]>([]);
  protected readonly filtroPacienteId = signal<number | null>(null);
  protected readonly editingId = signal<number | null>(null);
  protected readonly error = signal<string | null>(null);
  protected readonly success = signal<string | null>(null);
  protected readonly loading = signal(false);
  protected readonly form = this.fb.nonNullable.group({
    idPaciente: ['', Validators.required],
    fechaHora: ['', Validators.required],
    idProcedimiento: ['', Validators.required],
    observacionesDetalle: ['', Validators.maxLength(500)],
  });

  ngOnInit(): void {
    this.cargar();
    this.api.pacientes().subscribe({
      next: (items) => this.pacientes.set(items),
      error: () => this.error.set('No se pudo cargar el padrón de pacientes.'),
    });
    this.api.procedimientos().subscribe({
      next: (items) => this.procedimientos.set(items),
      error: () => this.error.set('No se pudo cargar el catálogo de procedimientos.'),
    });
  }

  cargar(): void {
    this.loading.set(true);
    this.api.citas(this.filtroPacienteId() ?? undefined).subscribe({
      next: (items) => this.items.set([...items].sort((a, b) => b.fechaHora.localeCompare(a.fechaHora))),
      error: () => {
        this.error.set('No se pudieron cargar las citas.');
        this.loading.set(false);
      },
      complete: () => this.loading.set(false),
    });
  }

  filtrarPorPaciente(event: Event): void {
    const target = event.target;
    if (!(target instanceof HTMLSelectElement)) return;
    this.filtroPacienteId.set(target.value ? Number(target.value) : null);
    this.cargar();
  }

  editar(item: Cita): void {
    if (item.estado === 'ATENDIDA' || item.estado === 'CANCELADA') return;
    this.editingId.set(item.id);
    this.form.setValue({
      idPaciente: String(item.idPaciente),
      fechaHora: item.fechaHora.slice(0, 16),
      idProcedimiento: '',
      observacionesDetalle: '',
    });
    this.detalles.set(item.detalles
      .filter((detalle) => detalle.idProcedimiento != null)
      .map((detalle) => ({
        idProcedimiento: detalle.idProcedimiento!,
        nombreProcedimiento: this.nombreProcedimiento(detalle.idProcedimiento!),
        observaciones: detalle.observaciones ?? '',
      })));
    this.error.set(null);
  }

  cancelarEdicion(): void {
    this.editingId.set(null);
    this.form.reset();
    this.detalles.set([]);
  }

  agregarProcedimiento(): void {
    const control = this.form.controls.idProcedimiento;
    control.markAsTouched();
    if (control.invalid) return;
    const idProcedimiento = Number(control.value);
    const procedimiento = this.procedimientos().find((item) => item.id === idProcedimiento);
    if (!procedimiento) return;
    if (this.detalles().some((detalle) => detalle.idProcedimiento === idProcedimiento)) {
      this.error.set('Ese procedimiento ya fue agregado a la cita.');
      return;
    }
    this.detalles.update((details) => [...details, {
      idProcedimiento,
      nombreProcedimiento: procedimiento.nombre,
      observaciones: this.form.controls.observacionesDetalle.value.trim(),
    }]);
    control.reset('');
    this.form.controls.observacionesDetalle.reset('');
    this.error.set(null);
  }

  quitarProcedimiento(idProcedimiento: number): void {
    this.detalles.update((details) => details.filter((detail) => detail.idProcedimiento !== idProcedimiento));
  }

  guardar(): void {
    this.error.set(null);
    this.success.set(null);
    if (this.form.controls.idPaciente.invalid || this.form.controls.fechaHora.invalid || this.detalles().length === 0) {
      this.form.markAllAsTouched();
      if (this.detalles().length === 0) this.error.set('Agrega al menos un procedimiento solicitado.');
      return;
    }
    const values = this.form.getRawValue();
    const body = {
      idPaciente: Number(values.idPaciente),
      fechaHora: `${values.fechaHora}:00`,
      detalles: this.detalles().map(({ idProcedimiento, observaciones }) => ({
        idProcedimiento,
        observaciones,
      })),
    };
    const id = this.editingId();
    this.loading.set(true);
    (id ? this.api.actualizarCita(id, body) : this.api.crearCita(body)).subscribe({
      next: () => {
        this.success.set(id ? 'Cita actualizada.' : 'Cita programada.');
        this.cancelarEdicion();
        this.cargar();
      },
      error: () => {
        this.error.set('No se pudo guardar la cita. Comprueba los datos y que el paciente exista.');
        this.loading.set(false);
      },
    });
  }

  cancelarCita(item: Cita): void {
    if (!confirm(`¿Cancelar la cita #${item.id}?`)) return;
    this.api.cancelarCita(item.id).subscribe({
      next: () => {
        this.items.update((items) => items.map((cita) => cita.id === item.id ? { ...cita, estado: 'CANCELADA' } : cita));
        this.success.set(`Cita #${item.id} cancelada.`);
      },
      error: () => this.error.set('No se pudo cancelar la cita. Una cita atendida no se puede cancelar.'),
    });
  }

  nombrePaciente(id: number): string {
    const paciente = this.pacientes().find((item) => item.id === id);
    return paciente ? `${paciente.apellidos}, ${paciente.nombres}` : `Paciente #${id}`;
  }

  nombreProcedimiento(id: number): string {
    return this.procedimientos().find((item) => item.id === id)?.nombre ?? `Procedimiento #${id}`;
  }

  pacienteSeleccionado(): string {
    const paciente = this.pacientes().find(
      (item) => item.id === Number(this.form.controls.idPaciente.value),
    );
    return paciente ? `${paciente.apellidos}, ${paciente.nombres}` : 'Paciente pendiente';
  }
}
