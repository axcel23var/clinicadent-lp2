import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ClinicApiService, Odontologo } from '../../../core/services/clinic-api.service';

@Component({
  selector: 'app-odontologo-page',
  imports: [ReactiveFormsModule],
  templateUrl: './odontologo-page.html',
})
export class OdontologoPage implements OnInit {
  private readonly api = inject(ClinicApiService);
  private readonly fb = inject(FormBuilder);
  protected readonly items = signal<Odontologo[]>([]);
  protected readonly editingId = signal<number | null>(null);
  protected readonly error = signal<string | null>(null);
  protected readonly success = signal<string | null>(null);
  protected readonly loading = signal(false);
  protected readonly form = this.fb.nonNullable.group({
    cop: ['', [Validators.required, Validators.maxLength(20)]],
    nombres: ['', [Validators.required, Validators.maxLength(80)]],
    apellidos: ['', [Validators.required, Validators.maxLength(80)]],
    especialidad: ['', [Validators.required, Validators.maxLength(100)]],
  });

  ngOnInit(): void { this.cargar(); }

  cargar(): void {
    this.loading.set(true);
    this.api.odontologos().subscribe({
      next: (items) => this.items.set(items),
      error: () => {
        this.error.set('No se pudieron cargar los odontólogos.');
        this.loading.set(false);
      },
      complete: () => this.loading.set(false),
    });
  }

  editar(item: Odontologo): void {
    this.editingId.set(item.id);
    this.form.setValue({ cop: item.cop, nombres: item.nombres, apellidos: item.apellidos, especialidad: item.especialidad });
    this.error.set(null);
  }

  cancelarEdicion(): void {
    this.editingId.set(null);
    this.form.reset();
  }

  guardar(): void {
    this.error.set(null);
    this.success.set(null);
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    const value = this.form.getRawValue();
    const request = { cop: value.cop.trim(), nombres: value.nombres.trim(), apellidos: value.apellidos.trim(), especialidad: value.especialidad.trim() };
    const id = this.editingId();
    this.loading.set(true);
    (id ? this.api.actualizarOdontologo(id, request) : this.api.crearOdontologo(request)).subscribe({
      next: () => {
        this.success.set(id ? 'Odontólogo actualizado.' : 'Odontólogo registrado.');
        this.cancelarEdicion();
        this.cargar();
      },
      error: () => { this.error.set('No se pudo guardar el odontólogo.'); this.loading.set(false); },
    });
  }

  eliminar(item: Odontologo): void {
    if (!confirm(`¿Eliminar a ${item.nombres} ${item.apellidos}?`)) return;
    this.api.eliminarOdontologo(item.id).subscribe({
      next: () => { this.items.update((items) => items.filter((odontologo) => odontologo.id !== item.id)); this.success.set('Odontólogo eliminado.'); },
      error: () => this.error.set('No se pudo eliminar. Comprueba si tiene atenciones asociadas.'),
    });
  }
}
