import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import {
  Atencion,
  Cita,
  ClinicApiService,
  Odontologo,
  Paciente,
  Procedimiento,
} from '../../../core/services/clinic-api.service';

interface DetalleAtencionDraft {
  idProcedimiento: number;
  nombreProcedimiento: string;
  piezaDental: string;
  observacion: string;
}

@Component({
  selector: 'app-atencion-page',
  imports: [DatePipe, DecimalPipe, ReactiveFormsModule],
  templateUrl: './atencion-page.html',
  styleUrl: '../../../shared/transaction-workflow.css',
})
export class AtencionPage implements OnInit {
  private readonly api = inject(ClinicApiService);
  private readonly fb = inject(FormBuilder);
  protected readonly items = signal<Atencion[]>([]);
  protected readonly citas = signal<Cita[]>([]);
  protected readonly todasLasCitas = signal<Cita[]>([]);
  protected readonly pacientes = signal<Paciente[]>([]);
  protected readonly detalles = signal<DetalleAtencionDraft[]>([]);
  protected readonly odontologos = signal<Odontologo[]>([]);
  protected readonly procedimientos = signal<Procedimiento[]>([]);
  protected readonly error = signal<string | null>(null);
  protected readonly success = signal<string | null>(null);
  protected readonly loading = signal(false);
  protected readonly saving = signal(false);

  protected readonly form = this.fb.nonNullable.group({
    idCita: ['', Validators.required],
    idOdontologo: ['', Validators.required],
    diagnostico: ['', [Validators.required, Validators.maxLength(500)]],
    notaMedica: ['', Validators.maxLength(500)],
    idProcedimiento: ['', Validators.required],
    piezaDental: ['', [Validators.required, Validators.maxLength(10)]],
    observacion: ['', Validators.maxLength(255)],
  });

  ngOnInit(): void {
    this.cargar();
    this.api.citas().subscribe({
      next: (citas) => {
        this.todasLasCitas.set(citas);
        this.citas.set(citas.filter((cita) => cita.estado === 'PROGRAMADA'));
      },
      error: () => this.error.set('No se pudieron cargar las citas disponibles.'),
    });
    this.api.pacientes().subscribe({
      next: (items) => this.pacientes.set(items),
      error: () => this.error.set('No se pudo cargar el padrón de pacientes.'),
    });
    this.api.odontologos().subscribe({
      next: (items) => this.odontologos.set(items),
      error: () => this.error.set('No se pudo cargar el listado de odontólogos.'),
    });
    this.api.procedimientos().subscribe({
      next: (items) => this.procedimientos.set(items),
      error: () => this.error.set('No se pudo cargar el catálogo de procedimientos.'),
    });
  }

  cargar(): void {
    this.loading.set(true);
    this.api.atenciones().subscribe({
      next: (items) => this.items.set(items),
      error: () => {
        this.error.set('No se pudieron cargar las atenciones.');
        this.loading.set(false);
      },
      complete: () => this.loading.set(false),
    });
  }

  guardar(): void {
    this.error.set(null);
    this.success.set(null);
    const { idCita, idOdontologo, diagnostico, notaMedica } = this.form.controls;
    if (idCita.invalid || idOdontologo.invalid || diagnostico.invalid || notaMedica.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    if (this.detalles().length === 0) {
      this.error.set('Agrega al menos una pieza dental y su intervención.');
      return;
    }
    const values = this.form.getRawValue();
    this.saving.set(true);
    this.api.registrarAtencion({
      idCita: Number(values.idCita),
      idOdontologo: Number(values.idOdontologo),
      diagnostico: values.diagnostico.trim(),
      notaMedica: values.notaMedica.trim(),
      detalles: this.detalles().map(({ idProcedimiento, piezaDental, observacion }) => ({
        idProcedimiento,
        piezaDental,
        observacion,
      })),
    }).subscribe({
      next: (atencion) => {
        this.items.update((items) => [atencion, ...items]);
        this.citas.update((citas) => citas.filter((cita) => cita.id !== atencion.idCita));
        this.detalles.set([]);
        this.form.reset();
        this.success.set(`Atención #${atencion.id} registrada. La cita quedó marcada como atendida.`);
        this.saving.set(false);
      },
      error: () => {
        this.error.set('No se pudo registrar la atención. Verifica que la cita siga programada y que el odontólogo exista.');
        this.saving.set(false);
      },
    });
  }

  agregarDetalle(): void {
    const controls = this.form.controls;
    controls.piezaDental.markAsTouched();
    controls.idProcedimiento.markAsTouched();
    if (controls.piezaDental.invalid || controls.idProcedimiento.invalid || controls.observacion.invalid) {
      return;
    }
    const values = this.form.getRawValue();
    const idProcedimiento = Number(values.idProcedimiento);
    const procedimiento = this.procedimientos().find((item) => item.id === idProcedimiento);
    if (!procedimiento) return;
    if (this.detalles().some((detail) =>
      detail.idProcedimiento === idProcedimiento && detail.piezaDental === values.piezaDental.trim())) {
      this.error.set('Ese procedimiento ya está registrado para la pieza indicada.');
      return;
    }
    this.detalles.update((current) => [
      ...current,
      {
        idProcedimiento,
        nombreProcedimiento: procedimiento.nombre,
        piezaDental: values.piezaDental.trim(),
        observacion: values.observacion.trim(),
      },
    ]);
    controls.idProcedimiento.reset('');
    controls.piezaDental.reset('');
    controls.observacion.reset('');
    this.error.set(null);
  }

  quitarDetalle(index: number): void {
    this.detalles.update((current) => current.filter((_, itemIndex) => itemIndex !== index));
  }

  pacienteDeCita(idCita: number): string {
    const cita = this.todasLasCitas().find((item) => item.id === idCita);
    if (!cita) return `Cita #${idCita}`;
    const paciente = this.pacientes().find((item) => item.id === cita.idPaciente);
    return paciente
      ? `${paciente.apellidos}, ${paciente.nombres} · Cita #${idCita}`
      : `Cita #${idCita}`;
  }

  nombreOdontologo(id: number): string {
    const odontologo = this.odontologos().find((item) => item.id === id);
    return odontologo ? `${odontologo.apellidos}, ${odontologo.nombres}` : `Odontólogo #${id}`;
  }
}
