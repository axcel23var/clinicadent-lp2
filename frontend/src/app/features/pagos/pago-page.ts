import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ClinicApiService, MetodoPago, Pago, Tratamiento } from '../../core/services/clinic-api.service';

interface DetalleAbonoDraft {
  idMetodoPago: number;
  nombreMetodo: string;
  montoAbonado: number;
  numeroOperacion: string;
}

interface DistribucionAbonoDraft {
  idTratamiento: number;
  nombreTratamiento: string;
  montoAplicado: number;
}

@Component({
  selector: 'app-pago-page',
  imports: [DatePipe, DecimalPipe, ReactiveFormsModule],
  templateUrl: './pago-page.html',
  styleUrls: ['../../shared/transaction-workflow.css', './pago-page.css'],
})
export class PagoPage implements OnInit {
  private readonly api = inject(ClinicApiService);
  private readonly fb = inject(FormBuilder);
  protected readonly items = signal<Pago[]>([]);
  protected readonly tratamientos = signal<Tratamiento[]>([]);
  protected readonly metodos = signal<MetodoPago[]>([]);
  protected readonly detallesAbono = signal<DetalleAbonoDraft[]>([]);
  protected readonly distribucionesAbono = signal<DistribucionAbonoDraft[]>([]);
  protected readonly error = signal<string | null>(null);
  protected readonly success = signal<string | null>(null);
  protected readonly loading = signal(false);
  protected readonly saving = signal(false);
  protected readonly pagoSeleccionado = signal<number | null>(null);
  protected readonly form = this.fb.nonNullable.group({
    idTratamiento: ['', Validators.required],
    montoTratamiento: [0, [Validators.required, Validators.min(0.01)]],
    idMetodoPago: ['', Validators.required],
    montoAbono: [0, [Validators.required, Validators.min(0.01)]],
    numeroOperacion: ['', Validators.maxLength(50)],
  });
  protected readonly metodoForm = this.fb.nonNullable.group({
    nombre: ['', [Validators.required, Validators.maxLength(50)]],
    descripcion: ['', Validators.maxLength(150)],
  });

  ngOnInit(): void {
    this.cargar();
    this.api.tratamientos().subscribe({
      next: (items) => this.tratamientos.set(items),
      error: () => this.error.set('No se pudieron cargar los tratamientos.'),
    });
    this.api.metodosPago().subscribe({
      next: (items) => this.metodos.set(items),
      error: () => this.error.set('No se pudieron cargar los métodos de pago.'),
    });
  }

  cargar(): void {
    this.loading.set(true);
    this.api.pagos().subscribe({
      next: (items) => this.items.set(items),
      error: () => {
        this.error.set('No se pudieron cargar los pagos.');
        this.loading.set(false);
      },
      complete: () => this.loading.set(false),
    });
  }

  guardar(): void {
    this.error.set(null);
    this.success.set(null);
    if (this.distribucionesAbono().length === 0) {
      this.error.set('Distribuye el pago entre al menos un tratamiento.');
      return;
    }
    if (this.detallesAbono().length === 0) {
      this.error.set('Agrega al menos un método al abono.');
      return;
    }
    const montoDistribuido = this.montoDistribuido();
    const montoTotal = this.detallesAbono().reduce((total, detail) => total + detail.montoAbonado, 0);
    if (Math.abs(montoTotal - montoDistribuido) > 0.009) {
      this.error.set('La suma de los métodos de pago debe coincidir con lo distribuido entre tratamientos.');
      return;
    }
    this.saving.set(true);
    this.api.registrarPago({
      distribuciones: this.distribucionesAbono().map(({ idTratamiento, montoAplicado }) => ({
        idTratamiento,
        montoAplicado,
      })),
      detalles: this.detallesAbono().map(({ idMetodoPago, montoAbonado, numeroOperacion }) => ({
        idMetodoPago,
        montoAbonado,
        numeroOperacion,
      })),
    }).subscribe({
      next: (pago) => {
        this.items.update((items) => [pago, ...items.filter((item) => item.id !== pago.id)]);
        this.tratamientos.update((items) => items.map((item) => {
          const distribucion = this.distribucionesAbono().find((detail) => detail.idTratamiento === item.id);
          return distribucion
            ? { ...item, saldoPendiente: item.saldoPendiente - distribucion.montoAplicado }
            : item;
        }));
        this.form.reset();
        this.detallesAbono.set([]);
        this.distribucionesAbono.set([]);
        this.success.set(`Abono registrado en el pago #${pago.id}.`);
        this.saving.set(false);
      },
      error: () => { this.error.set('No se pudo registrar el abono. Comprueba el saldo y el método de pago.'); this.saving.set(false); },
    });
  }

  agregarTratamientoAbono(): void {
    const { idTratamiento, montoTratamiento } = this.form.controls;
    idTratamiento.markAsTouched();
    montoTratamiento.markAsTouched();
    if (idTratamiento.invalid || montoTratamiento.invalid) return;

    const id = Number(idTratamiento.value);
    const tratamiento = this.tratamientos().find((item) => item.id === id);
    const monto = Number(montoTratamiento.value);
    if (!tratamiento || !Number.isFinite(monto) || monto <= 0) return;
    if (this.distribucionesAbono().some((detail) => detail.idTratamiento === id)) {
      this.error.set('Ese tratamiento ya está agregado a esta distribución.');
      return;
    }
    if (monto > tratamiento.saldoPendiente) {
      this.error.set('El monto aplicado no puede superar el saldo de ese tratamiento.');
      return;
    }
    this.distribucionesAbono.update((items) => [...items, {
      idTratamiento: id,
      nombreTratamiento: `Plan #${id} · Paciente #${tratamiento.idPaciente}`,
      montoAplicado: monto,
    }]);
    idTratamiento.reset('');
    montoTratamiento.reset(0);
    this.error.set(null);
  }

  quitarTratamientoAbono(idTratamiento: number): void {
    this.distribucionesAbono.update((items) => items.filter((item) => item.idTratamiento !== idTratamiento));
  }

  montoDistribuido(): number {
    return this.distribucionesAbono().reduce((total, item) => total + item.montoAplicado, 0);
  }

  agregarMetodoAbono(): void {
    const { idMetodoPago, montoAbono, numeroOperacion } = this.form.controls;
    idMetodoPago.markAsTouched();
    montoAbono.markAsTouched();
    if (idMetodoPago.invalid || montoAbono.invalid) return;

    const id = Number(idMetodoPago.value);
    const metodo = this.metodos().find((item) => item.id === id);
    const monto = Number(montoAbono.value);
    if (!metodo || !Number.isFinite(monto) || monto <= 0) return;
    if (this.distribucionesAbono().length === 0) {
      this.error.set('Agrega primero al menos un tratamiento a la distribución.');
      return;
    }
    if (this.detallesAbono().some((detail) => detail.idMetodoPago === id)) {
      this.error.set('Ese método ya está en el abono. Usa otro método o edita el monto.');
      return;
    }
    if (this.detallesAbono().reduce((total, detail) => total + detail.montoAbonado, monto) > this.montoDistribuido()) {
      this.error.set('La suma de los métodos no puede superar lo distribuido entre tratamientos.');
      return;
    }
    this.detallesAbono.update((details) => [...details, {
      idMetodoPago: id,
      nombreMetodo: metodo.nombre,
      montoAbonado: monto,
      numeroOperacion: numeroOperacion.value.trim(),
    }]);
    idMetodoPago.reset('');
    montoAbono.reset(0);
    numeroOperacion.reset('');
    this.error.set(null);
  }

  quitarMetodoAbono(idMetodoPago: number): void {
    this.detallesAbono.update((details) => details.filter((detail) => detail.idMetodoPago !== idMetodoPago));
  }

  totalAbono(): number {
    return this.detallesAbono().reduce((total, detail) => total + detail.montoAbonado, 0);
  }

  saldoSeleccionado(): number {
    return this.distribucionesAbono().reduce((total, detail) => total + this.saldo(detail.idTratamiento), 0);
  }

  saldoRestante(): number {
    return this.distribucionesAbono().reduce(
      (total, detail) => total + Math.max(0, this.saldo(detail.idTratamiento) - detail.montoAplicado),
      0,
    );
  }

  registrarMetodo(): void {
    this.error.set(null);
    if (this.metodoForm.invalid) { this.metodoForm.markAllAsTouched(); return; }
    const value = this.metodoForm.getRawValue();
    this.api.crearMetodoPago({ nombre: value.nombre.trim(), descripcion: value.descripcion.trim() }).subscribe({
      next: (metodo) => {
        this.metodos.update((items) => [...items, metodo]);
        this.metodoForm.reset();
        this.success.set(`Método de pago "${metodo.nombre}" registrado.`);
      },
      error: () => this.error.set('No se pudo registrar el método de pago.'),
    });
  }

  saldo(id: number): number {
    return this.tratamientos().find((item) => item.id === id)?.saldoPendiente ?? 0;
  }

  nombreMetodo(id: number): string {
    return this.metodos().find((item) => item.id === id)?.nombre ?? `Método #${id}`;
  }

  onTratamientoChange(event: Event): void {
    const target = event.target;
    if (!(target instanceof HTMLSelectElement)) return;
    const selected = this.tratamientos().find((item) => item.id === Number(target.value));
    this.form.controls.montoTratamiento.setValue(selected?.saldoPendiente ?? 0);
  }

  togglePago(id: number): void {
    this.pagoSeleccionado.update((actual) => (actual === id ? null : id));
  }
}
