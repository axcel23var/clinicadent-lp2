import { Component, OnInit, inject, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ClinicApiService, Especialidad, Procedimiento } from '../../../core/services/clinic-api.service';
import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'app-procedimiento-page',
  imports: [DecimalPipe, ReactiveFormsModule],
  templateUrl: './procedimiento-page.html',
})
export class ProcedimientoPage implements OnInit {
  private readonly api = inject(ClinicApiService);
  private readonly fb = inject(FormBuilder);
  protected readonly auth = inject(AuthService);
  protected readonly items = signal<Procedimiento[]>([]);
  protected readonly especialidades = signal<Especialidad[]>([]);
  protected readonly editingId = signal<number | null>(null);
  protected readonly error = signal<string | null>(null);
  protected readonly success = signal<string | null>(null);
  protected readonly loading = signal(false);
  protected readonly form = this.fb.nonNullable.group({
    idEspecialidad: [''],
    nombre: ['', [Validators.required, Validators.maxLength(100)]],
    precio: [0, [Validators.required, Validators.min(0)]],
  });

  ngOnInit(): void {
    this.cargar();
    this.api.especialidades().subscribe({
      next: (items) => this.especialidades.set(items),
      error: () => this.error.set('No se pudo cargar el catálogo de especialidades.'),
    });
  }

  cargar(): void {
    this.loading.set(true);
    this.api.procedimientos().subscribe({
      next: (items) => this.items.set(items),
      error: () => {
        this.error.set('No se pudo cargar el tarifario.');
        this.loading.set(false);
      },
      complete: () => this.loading.set(false),
    });
  }

  editar(item: Procedimiento): void {
    this.editingId.set(item.id);
    this.form.setValue({
      idEspecialidad: item.idEspecialidad == null ? '' : String(item.idEspecialidad),
      nombre: item.nombre,
      precio: item.precio,
    });
    this.error.set(null);
  }

  cancelarEdicion(): void {
    this.editingId.set(null);
    this.form.reset({ idEspecialidad: '', nombre: '', precio: 0 });
  }

  guardar(): void {
    this.error.set(null);
    this.success.set(null);
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const values = this.form.getRawValue();
    const request = {
      idEspecialidad: values.idEspecialidad ? Number(values.idEspecialidad) : null,
      nombre: values.nombre.trim(),
      precio: Number(values.precio),
    };
    const id = this.editingId();
    this.loading.set(true);
    (id ? this.api.actualizarProcedimiento(id, request) : this.api.crearProcedimiento(request)).subscribe({
      next: () => {
        this.success.set(id ? 'Procedimiento actualizado.' : 'Procedimiento registrado.');
        this.cancelarEdicion();
        this.cargar();
      },
      error: () => {
        this.error.set('No se pudo guardar el procedimiento. Verifica el nombre y el precio.');
        this.loading.set(false);
      },
    });
  }

  eliminar(item: Procedimiento): void {
    if (!confirm(`¿Eliminar el procedimiento "${item.nombre}"?`)) return;
    this.api.eliminarProcedimiento(item.id).subscribe({
      next: () => {
        this.items.update((items) => items.filter((procedure) => procedure.id !== item.id));
        this.success.set('Procedimiento eliminado.');
      },
      error: () => this.error.set('No se pudo eliminar. El procedimiento podría estar en un plan existente.'),
    });
  }

  nombreEspecialidad(id: number | null): string {
    if (id == null) return 'Sin especialidad';
    return this.especialidades().find((item) => item.id === id)?.nombre ?? `Especialidad #${id}`;
  }
}
