import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { EspecialidadService } from './especialidad.service';

@Component({
  selector: 'app-especialidad-form',
  imports: [ReactiveFormsModule],
  templateUrl: './especialidad-form.html',
})
export class EspecialidadForm implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly especialidadService = inject(EspecialidadService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  protected readonly id = signal<number | null>(null);
  protected readonly error = signal<string | null>(null);
  protected readonly loading = signal(false);
  protected readonly errorCarga = signal(false);
  protected readonly editando = signal(false);

  protected readonly form = this.formBuilder.nonNullable.group({
    nombre: ['', [Validators.required, Validators.maxLength(100)]],
    descripcion: ['', [Validators.maxLength(200)]],
  });

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (!idParam) return;

    const id = Number(idParam);
    if (!Number.isInteger(id) || id <= 0) {
      this.errorCarga.set(true);
      this.error.set('El identificador de la especialidad no es válido.');
      return;
    }

    this.id.set(id);
    this.editando.set(true);
    this.loading.set(true);
    this.especialidadService.obtener(id).subscribe({
      next: (especialidad) => {
        this.form.patchValue({
          nombre: especialidad.nombre,
          descripcion: especialidad.descripcion ?? '',
        });
        this.loading.set(false);
      },
      error: () => {
        this.errorCarga.set(true);
        this.error.set('No se pudo cargar la especialidad solicitada.');
        this.loading.set(false);
      },
    });
  }

  guardar(): void {
    if (this.loading() || this.errorCarga()) return;
    this.error.set(null);

    const nombre = this.form.controls.nombre;
    nombre.setValue(nombre.value.trim());
    const descripcion = this.form.controls.descripcion;
    descripcion.setValue(descripcion.value.trim());

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const valor = this.form.getRawValue();
    const id = this.id();
    const peticion = id
      ? this.especialidadService.actualizar(id, valor)
      : this.especialidadService.crear(valor);

    this.loading.set(true);
    peticion.subscribe({
      next: () => this.router.navigate(['/catalogo/especialidades']),
      error: () => {
        this.error.set('No se pudo guardar. Revisa los datos e inténtalo nuevamente.');
        this.loading.set(false);
      },
    });
  }

  cancelar(): void {
    this.router.navigate(['/catalogo/especialidades']);
  }

  protected mensajeValidacion(campo: 'nombre' | 'descripcion'): string {
    const control = this.form.controls[campo];
    if (!control.touched) return '';
    if (control.hasError('required')) return 'Este campo es obligatorio.';
    if (control.hasError('maxlength')) {
      return `Máximo ${control.getError('maxlength').requiredLength} caracteres.`;
    }
    return '';
  }
}
