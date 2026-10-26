import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { DatePipe, DecimalPipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { forkJoin } from 'rxjs';
import {
  DetalleTratamientoDraft,
  Paciente,
  Procedimiento,
  Tratamiento,
} from './tratamiento.model';
import { TratamientoService } from './tratamiento.service';

@Component({
  selector: 'app-tratamiento-list',
  imports: [DatePipe, DecimalPipe, ReactiveFormsModule],
  templateUrl: './tratamiento-list.html',
  styleUrl: './tratamiento-list.css',
})
export class TratamientoList implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly tratamientoService = inject(TratamientoService);

  protected readonly pacientes = signal<Paciente[]>([]);
  protected readonly procedimientos = signal<Procedimiento[]>([]);
  protected readonly tratamientos = signal<Tratamiento[]>([]);
  protected readonly detalles = signal<DetalleTratamientoDraft[]>([]);
  protected readonly loading = signal(false);
  protected readonly saving = signal(false);
  protected readonly selectedPlanId = signal<number | null>(null);
  protected readonly savingPlanId = signal<number | null>(null);
  protected readonly error = signal<string | null>(null);
  protected readonly success = signal<string | null>(null);

  protected readonly form = this.formBuilder.nonNullable.group({
    idPaciente: ['', Validators.required],
    idProcedimiento: ['', Validators.required],
    cantidad: [1, [Validators.required, Validators.min(1)]],
  });
  protected readonly addForm = this.formBuilder.nonNullable.group({
    idProcedimiento: ['', Validators.required],
    cantidad: [1, [Validators.required, Validators.min(1)]],
  });

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.loading.set(true);
    this.error.set(null);
    forkJoin({
      pacientes: this.tratamientoService.listarPacientes(),
      procedimientos: this.tratamientoService.listarProcedimientos(),
      tratamientos: this.tratamientoService.listarTratamientos(),
    }).subscribe({
      next: ({ pacientes, procedimientos, tratamientos }) => {
        this.pacientes.set(pacientes);
        this.procedimientos.set(procedimientos);
        this.tratamientos.set(tratamientos);
        this.loading.set(false);
      },
      error: () => {
        this.error.set(
          'No se pudieron cargar pacientes, procedimientos y tratamientos. Verifica que el backend esté activo.',
        );
        this.loading.set(false);
      },
    });
  }

  agregarProcedimiento(): void {
    const { idProcedimiento, cantidad } = this.form.getRawValue();
    const procedimientoId = Number(idProcedimiento);
    const procedimiento = this.procedimientos().find(
      (item) => item.id === procedimientoId,
    );

    if (!procedimiento || !Number.isInteger(cantidad) || cantidad < 1) {
      this.form.controls.idProcedimiento.markAsTouched();
      this.form.controls.cantidad.markAsTouched();
      return;
    }

    if (this.detalles().some((detalle) => detalle.idProcedimiento === procedimientoId)) {
      this.error.set('Ese procedimiento ya está agregado. Puedes cambiar su cantidad.');
      return;
    }

    this.error.set(null);
    this.detalles.update((current) => [
      ...current,
      {
        idProcedimiento: procedimiento.id,
        nombreProcedimiento: procedimiento.nombre,
        precioUnitario: procedimiento.precio,
        cantidad,
      },
    ]);
    this.form.controls.idProcedimiento.setValue('');
    this.form.controls.cantidad.setValue(1);
  }

  quitarProcedimiento(procedimientoId: number): void {
    this.detalles.update((current) =>
      current.filter((detalle) => detalle.idProcedimiento !== procedimientoId),
    );
  }

  seleccionarPlan(tratamientoId: number): void {
    this.selectedPlanId.set(tratamientoId);
    this.error.set(null);
  }

  cancelarAgregarProcedimiento(): void {
    this.selectedPlanId.set(null);
    this.addForm.reset({ idProcedimiento: '', cantidad: 1 });
  }

  agregarAPlan(): void {
    const tratamientoId = this.selectedPlanId();
    const values = this.addForm.getRawValue();
    const procedimiento = this.procedimientos().find(
      (item) => item.id === Number(values.idProcedimiento),
    );
    const cantidad = Number(values.cantidad);
    if (tratamientoId == null || !procedimiento || !Number.isInteger(cantidad) || cantidad < 1) {
      this.error.set('Selecciona un procedimiento y una cantidad válida.');
      return;
    }
    this.error.set(null);
    this.savingPlanId.set(tratamientoId);
    this.tratamientoService.agregarProcedimiento(tratamientoId, {
      idProcedimiento: procedimiento.id,
      cantidad,
    }).subscribe({
      next: (updated) => {
        this.tratamientos.update((items) =>
          items.map((item) => (item.id === updated.id ? updated : item)),
        );
        this.cancelarAgregarProcedimiento();
        this.success.set(`Procedimiento agregado al plan #${updated.id}.`);
        this.savingPlanId.set(null);
      },
      error: () => {
        this.error.set('No se pudo agregar el procedimiento. Comprueba que el plan no esté finalizado.');
        this.savingPlanId.set(null);
      },
    });
  }

  cambiarEstado(tratamientoId: number, event: Event): void {
    const target = event.target;
    if (!(target instanceof HTMLSelectElement)) return;
    const estado = target.value;
    const estadoAnterior = this.tratamientos().find(
      (item) => item.id === tratamientoId,
    )?.estado;
    this.error.set(null);
    this.tratamientoService.actualizarEstado(tratamientoId, estado).subscribe({
      next: (updated) => {
        this.tratamientos.update((items) =>
          items.map((item) => (item.id === updated.id ? updated : item)),
        );
        this.success.set(`Estado del plan #${updated.id} actualizado.`);
      },
      error: () => {
        if (estadoAnterior) target.value = estadoAnterior;
        this.error.set('No se pudo actualizar el estado del tratamiento.');
      },
    });
  }

  guardar(): void {
    this.error.set(null);
    this.success.set(null);
    this.form.controls.idPaciente.markAsTouched();

    if (this.form.controls.idPaciente.invalid || this.detalles().length === 0) {
      if (this.detalles().length === 0) {
        this.error.set('Agrega al menos un procedimiento para crear el plan.');
      }
      return;
    }

    const request = {
      idPaciente: Number(this.form.controls.idPaciente.value),
      detalles: this.detalles().map(({ idProcedimiento, cantidad }) => ({
        idProcedimiento,
        cantidad,
      })),
    };

    this.saving.set(true);
    this.tratamientoService.crear(request).subscribe({
      next: (tratamiento) => {
        this.tratamientos.update((current) => [tratamiento, ...current]);
        this.detalles.set([]);
        this.form.reset({ idPaciente: '', idProcedimiento: '', cantidad: 1 });
        this.success.set(`Se creó correctamente el plan #${tratamiento.id}.`);
        this.saving.set(false);
      },
      error: (response: HttpErrorResponse) => {
        this.error.set(this.mensajeError(response));
        this.saving.set(false);
      },
    });
  }

  nombrePaciente(idPaciente: number): string {
    const paciente = this.pacientes().find((item) => item.id === idPaciente);
    return paciente
      ? `${paciente.nombres} ${paciente.apellidos}`
      : `Paciente #${idPaciente}`;
  }

  nombreProcedimiento(idProcedimiento: number): string {
    return (
      this.procedimientos().find((item) => item.id === idProcedimiento)?.nombre ??
      `Procedimiento #${idProcedimiento}`
    );
  }

  estadosDisponibles(estado: string): string[] {
    const siguiente: Record<string, string> = {
      BORRADOR: 'APROBADO',
      APROBADO: 'EN_PROCESO',
      EN_PROCESO: 'FINALIZADO',
    };
    return siguiente[estado] ? [estado, siguiente[estado]] : [estado];
  }

  totalEstimado(): number {
    return this.detalles().reduce(
      (total, detalle) => total + detalle.precioUnitario * detalle.cantidad,
      0,
    );
  }

  private mensajeError(response: HttpErrorResponse): string {
    if (response.status === 0) {
      return 'No se pudo conectar con el backend. Comprueba que esté activo.';
    }
    if (response.status === 400) {
      return 'El backend rechazó los datos. Revisa el paciente, los procedimientos y las cantidades.';
    }
    return 'No se pudo crear el plan de tratamiento. Inténtalo nuevamente.';
  }
}
