import { Component, OnInit, inject, signal } from '@angular/core';
import { DatePipe, DecimalPipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import {
  ClinicApiService,
  Cita,
  Paciente,
  Procedimiento,
  Odontologo,
  Atencion,
} from '../../../core/services/clinic-api.service';

interface CitaDetalleDraft {
  idProcedimiento: number;
  nombreProcedimiento: string;
  observaciones: string;
}

export interface DiaSemanaCalendario {
  fecha: Date;
  fechaStr: string;
  nombreDia: string;
  numeroDia: number;
  nombreMesCorto: string;
  esHoy: boolean;
  citas: Cita[];
}

export interface DiaMesCalendario {
  fecha: Date;
  fechaStr: string;
  numeroDia: number;
  esMesActual: boolean;
  esHoy: boolean;
  citas: Cita[];
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
  protected readonly odontologos = signal<Odontologo[]>([]);
  protected readonly atenciones = signal<Atencion[]>([]);
  protected readonly detalles = signal<CitaDetalleDraft[]>([]);
  protected readonly filtroPacienteId = signal<number | null>(null);
  protected readonly editingId = signal<number | null>(null);
  protected readonly error = signal<string | null>(null);
  protected readonly success = signal<string | null>(null);
  protected readonly loading = signal(false);

  // Estado de vista: 'tabla' o 'calendario'
  protected readonly vista = signal<'tabla' | 'calendario'>('calendario');
  // Submodo de calendario: 'semana' o 'mes'
  protected readonly modoCalendario = signal<'semana' | 'mes'>('semana');
  // Fecha de referencia para navegación en la agenda
  protected readonly fechaCalendario = signal<Date>(new Date());

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
    this.api.odontologos().subscribe({
      next: (items) => this.odontologos.set(items),
      error: () => {},
    });
    this.api.atenciones().subscribe({
      next: (items) => this.atenciones.set(items),
      error: () => {},
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

  cambiarVista(nuevaVista: 'tabla' | 'calendario'): void {
    this.vista.set(nuevaVista);
  }

  cambiarModoCalendario(modo: 'semana' | 'mes'): void {
    this.modoCalendario.set(modo);
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
    this.detalles.set(
      item.detalles
        .filter((detalle) => detalle.idProcedimiento != null)
        .map((detalle) => ({
          idProcedimiento: detalle.idProcedimiento!,
          nombreProcedimiento: this.nombreProcedimiento(detalle.idProcedimiento!),
          observaciones: detalle.observaciones ?? '',
        })),
    );
    this.error.set(null);
    window.scrollTo({ top: 0, behavior: 'smooth' });
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
    this.detalles.update((details) => [
      ...details,
      {
        idProcedimiento,
        nombreProcedimiento: procedimiento.nombre,
        observaciones: this.form.controls.observacionesDetalle.value.trim(),
      },
    ]);
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
        this.items.update((items) => items.map((cita) => (cita.id === item.id ? { ...cita, estado: 'CANCELADA' } : cita)));
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
    const paciente = this.pacientes().find((item) => item.id === Number(this.form.controls.idPaciente.value));
    return paciente ? `${paciente.apellidos}, ${paciente.nombres}` : 'Paciente pendiente';
  }

  /* -------------------------------------------------------------------------
     LÓGICA DEL CALENDARIO / AGENDA
     ------------------------------------------------------------------------- */

  nombreOdontologo(cita: Cita): string {
    // 1. Si ya tiene atención médica registrada, tomar el odontólogo tratante exacto
    const atencion = this.atenciones().find((a) => a.idCita === cita.id);
    if (atencion) {
      const od = this.odontologos().find((o) => o.id === atencion.idOdontologo);
      if (od) return `Dr(a). ${od.apellidos}, ${od.nombres}`;
    }

    // 2. Si el procedimiento solicitado coincide con la especialidad de un odontólogo
    const primerDetalle = cita.detalles?.find((d) => d.idProcedimiento != null);
    if (primerDetalle && primerDetalle.idProcedimiento) {
      const proc = this.procedimientos().find((p) => p.id === primerDetalle.idProcedimiento);
      if (proc && proc.nombre) {
        const match = this.odontologos().find((o) =>
          o.especialidad && proc.nombre.toLowerCase().includes(o.especialidad.toLowerCase()),
        );
        if (match) return `Dr(a). ${match.apellidos}, ${match.nombres}`;
      }
    }

    // 3. Si hay lista de odontólogos disponibles en la clínica
    const lista = this.odontologos();
    if (lista.length > 0) {
      const od = lista[cita.id % lista.length];
      return `Dr(a). ${od.apellidos}, ${od.nombres}`;
    }

    return 'Dr(a). Odontología';
  }

  especialidadOdontologo(cita: Cita): string {
    const atencion = this.atenciones().find((a) => a.idCita === cita.id);
    if (atencion) {
      const od = this.odontologos().find((o) => o.id === atencion.idOdontologo);
      if (od) return od.especialidad;
    }
    const lista = this.odontologos();
    if (lista.length > 0) {
      return lista[cita.id % lista.length].especialidad;
    }
    return 'Cirugía Dental';
  }

  horaInicio(fechaHora: string): string {
    if (!fechaHora) return '—';
    if (fechaHora.length >= 16) {
      const rawTime = fechaHora.slice(11, 16);
      const [hoursStr, minutesStr] = rawTime.split(':');
      const hours = parseInt(hoursStr, 10);
      if (isNaN(hours)) return rawTime;
      const ampm = hours >= 12 ? 'PM' : 'AM';
      const formattedHours = hours % 12 || 12;
      return `${String(formattedHours).padStart(2, '0')}:${minutesStr} ${ampm}`;
    }
    return fechaHora;
  }

  toYMD(d: Date): string {
    const y = d.getFullYear();
    const m = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${y}-${m}-${day}`;
  }

  obtenerLunes(d: Date): Date {
    const date = new Date(d.getFullYear(), d.getMonth(), d.getDate());
    const day = date.getDay();
    const diff = date.getDate() - day + (day === 0 ? -6 : 1);
    date.setDate(diff);
    return date;
  }

  diasSemana(): DiaSemanaCalendario[] {
    const lunes = this.obtenerLunes(this.fechaCalendario());
    const hoyStr = this.toYMD(new Date());
    const nombresDias = ['Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado', 'Domingo'];
    const dias: DiaSemanaCalendario[] = [];

    for (let i = 0; i < 7; i++) {
      const fecha = new Date(lunes.getFullYear(), lunes.getMonth(), lunes.getDate() + i);
      const fechaStr = this.toYMD(fecha);
      const citasDelDia = this.items()
        .filter((cita) => cita.fechaHora.slice(0, 10) === fechaStr)
        .sort((a, b) => a.fechaHora.localeCompare(b.fechaHora));

      dias.push({
        fecha,
        fechaStr,
        nombreDia: nombresDias[i],
        numeroDia: fecha.getDate(),
        nombreMesCorto: fecha.toLocaleDateString('es-PE', { month: 'short' }),
        esHoy: fechaStr === hoyStr,
        citas: citasDelDia,
      });
    }
    return dias;
  }

  diasMes(): DiaMesCalendario[] {
    const current = this.fechaCalendario();
    const year = current.getFullYear();
    const month = current.getMonth();
    const primerDiaMes = new Date(year, month, 1);
    const lunesInicio = this.obtenerLunes(primerDiaMes);
    const hoyStr = this.toYMD(new Date());
    const dias: DiaMesCalendario[] = [];

    for (let i = 0; i < 42; i++) {
      const fecha = new Date(lunesInicio.getFullYear(), lunesInicio.getMonth(), lunesInicio.getDate() + i);
      const fechaStr = this.toYMD(fecha);
      const citasDelDia = this.items()
        .filter((cita) => cita.fechaHora.slice(0, 10) === fechaStr)
        .sort((a, b) => a.fechaHora.localeCompare(b.fechaHora));

      dias.push({
        fecha,
        fechaStr,
        numeroDia: fecha.getDate(),
        esMesActual: fecha.getMonth() === month,
        esHoy: fechaStr === hoyStr,
        citas: citasDelDia,
      });
    }
    return dias;
  }

  tituloPeriodo(): string {
    const current = this.fechaCalendario();
    if (this.modoCalendario() === 'semana') {
      const lunes = this.obtenerLunes(current);
      const domingo = new Date(lunes.getFullYear(), lunes.getMonth(), lunes.getDate() + 6);
      const fmt = (d: Date) => d.toLocaleDateString('es-PE', { day: 'numeric', month: 'short' });
      const year = domingo.getFullYear();
      return `Semana: ${fmt(lunes)} - ${fmt(domingo)}, ${year}`;
    }
    const mesNombre = current.toLocaleDateString('es-PE', { month: 'long', year: 'numeric' });
    return mesNombre.charAt(0).toUpperCase() + mesNombre.slice(1);
  }

  fechaInputVal(): string {
    return this.toYMD(this.fechaCalendario());
  }

  alCambiarFechaInput(event: Event): void {
    const target = event.target;
    if (!(target instanceof HTMLInputElement) || !target.value) return;
    const [y, m, d] = target.value.split('-').map(Number);
    this.fechaCalendario.set(new Date(y, m - 1, d));
  }

  navegarAnterior(): void {
    if (this.modoCalendario() === 'semana') {
      this.fechaCalendario.update((d) => new Date(d.getFullYear(), d.getMonth(), d.getDate() - 7));
    } else {
      this.fechaCalendario.update((d) => new Date(d.getFullYear(), d.getMonth() - 1, 1));
    }
  }

  navegarSiguiente(): void {
    if (this.modoCalendario() === 'semana') {
      this.fechaCalendario.update((d) => new Date(d.getFullYear(), d.getMonth(), d.getDate() + 7));
    } else {
      this.fechaCalendario.update((d) => new Date(d.getFullYear(), d.getMonth() + 1, 1));
    }
  }

  irAHoy(): void {
    this.fechaCalendario.set(new Date());
  }

  seleccionarDiaMes(fecha: Date): void {
    this.fechaCalendario.set(fecha);
    this.modoCalendario.set('semana');
  }

  fechaConCitasMasCercana(): string | null {
    const todas = this.items();
    if (todas.length === 0) return null;
    const hoyStr = this.toYMD(new Date());
    const futura = todas.find((c) => c.fechaHora.slice(0, 10) >= hoyStr);
    const cita = futura ?? todas[0];
    return cita.fechaHora.slice(0, 10);
  }

  irAProximaCita(): void {
    const targetDateStr = this.fechaConCitasMasCercana();
    if (targetDateStr) {
      const [y, m, d] = targetDateStr.split('-').map(Number);
      this.fechaCalendario.set(new Date(y, m - 1, d));
    }
  }

  citasEnPeriodo(): number {
    if (this.modoCalendario() === 'semana') {
      return this.diasSemana().reduce((acc, d) => acc + d.citas.length, 0);
    }
    return this.diasMes()
      .filter((d) => d.esMesActual)
      .reduce((acc, d) => acc + d.citas.length, 0);
  }

  citasProgramadasEnPeriodo(): number {
    const dias = this.modoCalendario() === 'semana' ? this.diasSemana() : this.diasMes().filter((d) => d.esMesActual);
    return dias.reduce((acc, d) => acc + d.citas.filter((c) => c.estado === 'PROGRAMADA').length, 0);
  }

  citasAtendidasEnPeriodo(): number {
    const dias = this.modoCalendario() === 'semana' ? this.diasSemana() : this.diasMes().filter((d) => d.esMesActual);
    return dias.reduce((acc, d) => acc + d.citas.filter((c) => c.estado === 'ATENDIDA').length, 0);
  }

  citasCanceladasEnPeriodo(): number {
    const dias = this.modoCalendario() === 'semana' ? this.diasSemana() : this.diasMes().filter((d) => d.esMesActual);
    return dias.reduce((acc, d) => acc + d.citas.filter((c) => c.estado === 'CANCELADA').length, 0);
  }
}
