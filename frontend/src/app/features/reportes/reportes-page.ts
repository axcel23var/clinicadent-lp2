import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ReporteClinico, ClinicApiService } from '../../core/services/clinic-api.service';

@Component({
  selector: 'app-reportes-page',
  imports: [CurrencyPipe, DatePipe, ReactiveFormsModule],
  templateUrl: './reportes-page.html',
  styleUrl: './reportes-page.css',
})
export class ReportesPage implements OnInit {
  private readonly api = inject(ClinicApiService);
  private readonly fb = inject(FormBuilder);

  protected readonly reporte = signal<ReporteClinico | null>(null);
  protected readonly loading = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly form = this.fb.nonNullable.group({
    desde: ['', Validators.required],
    hasta: ['', Validators.required],
  });

  ngOnInit(): void {
    const hoy = this.fechaLocal(new Date());
    const inicioMes = this.fechaLocal(new Date(new Date().getFullYear(), new Date().getMonth(), 1));
    this.form.setValue({ desde: inicioMes, hasta: hoy });
    this.consultar();
  }

  consultar(): void {
    this.error.set(null);
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const { desde, hasta } = this.form.getRawValue();
    if (desde > hasta) {
      this.error.set('La fecha inicial no puede ser posterior a la fecha final.');
      return;
    }
    this.loading.set(true);
    this.api.reporteClinico(desde, hasta).subscribe({
      next: (reporte) => {
        this.reporte.set(reporte);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('No se pudo generar el reporte. Revisa el rango y vuelve a intentar.');
        this.loading.set(false);
      },
    });
  }

  anchoBarra(total: number): number {
    const maximo = Math.max(...(this.reporte()?.recaudacionDiaria.map((item) => item.total) ?? [0]));
    return maximo > 0 ? Math.max(5, (total / maximo) * 100) : 0;
  }

  private fechaLocal(fecha: Date): string {
    const local = new Date(fecha.getTime() - fecha.getTimezoneOffset() * 60000);
    return local.toISOString().slice(0, 10);
  }
}
