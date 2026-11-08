import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

export interface Paciente {
  id: number;
  dni: string;
  nombres: string;
  apellidos: string;
  telefono?: string;
}

export interface PacienteRequest {
  dni: string;
  nombres: string;
  apellidos: string;
  telefono: string;
}

export interface CitaDetalle {
  id: number;
  idProcedimiento: number | null;
  motivoConsulta: string;
  observaciones?: string;
}

export interface Cita {
  id: number;
  idPaciente: number;
  fechaHora: string;
  estado: string;
  detalles: CitaDetalle[];
}

export interface CitaRequest {
  idPaciente: number;
  fechaHora: string;
  detalles: { idProcedimiento: number; observaciones?: string }[];
}

export interface Especialidad {
  id: number;
  nombre: string;
  descripcion?: string;
}

export interface Procedimiento {
  id: number;
  idEspecialidad: number | null;
  nombre: string;
  precio: number;
}

export interface ProcedimientoRequest {
  idEspecialidad: number | null;
  nombre: string;
  precio: number;
}

export interface Odontologo {
  id: number;
  cop: string;
  nombres: string;
  apellidos: string;
  especialidad: string;
}

export interface OdontologoRequest {
  cop: string;
  nombres: string;
  apellidos: string;
  especialidad: string;
}

export interface AtencionDetalle {
  id: number;
  idProcedimiento: number | null;
  piezaDental: string;
  intervencion: string;
  observacion?: string;
}

export interface Atencion {
  id: number;
  idCita: number;
  idOdontologo: number;
  faAtencion: string;
  diagnostico: string;
  notaMedica?: string;
  detalles: AtencionDetalle[];
}

export interface MetodoPago {
  id: number;
  nombre: string;
  descripcion?: string;
}

export interface PagoDetalle {
  id: number;
  idMetodoPago: number;
  montoAbonado: number;
  numeroOperacion?: string;
}

export interface PagoDistribucion {
  id: number;
  idTratamiento: number;
  montoAplicado: number;
}

export interface Pago {
  id: number;
  idTratamiento: number;
  fechaPago: string;
  montoTotal: number;
  estado: string;
  detalles: PagoDetalle[];
  distribuciones: PagoDistribucion[];
}

export interface Tratamiento {
  id: number;
  idPaciente: number;
  fechaCreacion: string;
  estado: string;
  costoTotal: number;
  saldoPendiente: number;
  detalles: { id: number; idProcedimiento: number; cantidad: number; subtotal: number }[];
}

export interface ReporteClinico {
  desde: string;
  hasta: string;
  totalCitas: number;
  citasProgramadas: number;
  citasAtendidas: number;
  citasCanceladas: number;
  totalAtenciones: number;
  tratamientosActivos: number;
  saldoPendienteActual: number;
  cantidadPagos: number;
  totalRecaudado: number;
  procedimientosMasRealizados: { procedimiento: string; vecesRealizado: number }[];
  recaudacionDiaria: { fecha: string; total: number }[];
  recaudacionPorMetodo: { metodo: string; total: number }[];
}

@Injectable({ providedIn: 'root' })
export class ClinicApiService {
  private readonly http = inject(HttpClient);
  private readonly api = inject(ApiService);

  private url(path: string): string {
    return this.api.buildUrl(`/api/v1/${path}`);
  }

  pacientes(): Observable<Paciente[]> {
    return this.http.get<Paciente[]>(this.url('pacientes'));
  }

  crearPaciente(request: PacienteRequest): Observable<Paciente> {
    return this.http.post<Paciente>(this.url('pacientes'), request);
  }

  actualizarPaciente(id: number, request: PacienteRequest): Observable<Paciente> {
    return this.http.put<Paciente>(this.url(`pacientes/${id}`), request);
  }

  eliminarPaciente(id: number): Observable<void> {
    return this.http.delete<void>(this.url(`pacientes/${id}`));
  }

  citas(idPaciente?: number): Observable<Cita[]> {
    return this.http.get<Cita[]>(this.url('citas'), {
      params: idPaciente == null ? {} : { idPaciente },
    });
  }

  crearCita(request: CitaRequest): Observable<Cita> {
    return this.http.post<Cita>(this.url('citas'), request);
  }

  actualizarCita(id: number, request: CitaRequest): Observable<Cita> {
    return this.http.put<Cita>(this.url(`citas/${id}`), request);
  }

  cancelarCita(id: number): Observable<{ id: string; estado: string }> {
    return this.http.post<{ id: string; estado: string }>(
      this.url(`citas/${id}/cancelar`),
      {},
    );
  }

  especialidades(): Observable<Especialidad[]> {
    return this.http.get<Especialidad[]>(this.url('especialidades'));
  }

  procedimientos(): Observable<Procedimiento[]> {
    return this.http.get<Procedimiento[]>(this.url('procedimientos'));
  }

  crearProcedimiento(request: ProcedimientoRequest): Observable<Procedimiento> {
    return this.http.post<Procedimiento>(this.url('procedimientos'), request);
  }

  actualizarProcedimiento(
    id: number,
    request: ProcedimientoRequest,
  ): Observable<Procedimiento> {
    return this.http.put<Procedimiento>(this.url(`procedimientos/${id}`), request);
  }

  eliminarProcedimiento(id: number): Observable<void> {
    return this.http.delete<void>(this.url(`procedimientos/${id}`));
  }

  odontologos(): Observable<Odontologo[]> {
    return this.http.get<Odontologo[]>(this.url('odontologos'));
  }

  crearOdontologo(request: OdontologoRequest): Observable<Odontologo> {
    return this.http.post<Odontologo>(this.url('odontologos'), request);
  }

  actualizarOdontologo(
    id: number,
    request: OdontologoRequest,
  ): Observable<Odontologo> {
    return this.http.put<Odontologo>(this.url(`odontologos/${id}`), request);
  }

  eliminarOdontologo(id: number): Observable<void> {
    return this.http.delete<void>(this.url(`odontologos/${id}`));
  }

  atenciones(): Observable<Atencion[]> {
    return this.http.get<Atencion[]>(this.url('atenciones'));
  }

  registrarAtencion(request: {
    idCita: number;
    idOdontologo: number;
    diagnostico: string;
    notaMedica: string;
    detalles: {
      idProcedimiento: number;
      piezaDental: string;
      observacion: string;
    }[];
  }): Observable<Atencion> {
    return this.http.post<Atencion>(this.url('atenciones'), request);
  }

  metodosPago(): Observable<MetodoPago[]> {
    return this.http.get<MetodoPago[]>(this.url('metodos-pago'));
  }

  crearMetodoPago(request: { nombre: string; descripcion: string }): Observable<MetodoPago> {
    return this.http.post<MetodoPago>(this.url('metodos-pago'), request);
  }

  actualizarMetodoPago(
    id: number,
    request: { nombre: string; descripcion: string },
  ): Observable<MetodoPago> {
    return this.http.put<MetodoPago>(this.url(`metodos-pago/${id}`), request);
  }

  eliminarMetodoPago(id: number): Observable<void> {
    return this.http.delete<void>(this.url(`metodos-pago/${id}`));
  }

  pagos(): Observable<Pago[]> {
    return this.http.get<Pago[]>(this.url('pagos'));
  }

  reporteClinico(desde: string, hasta: string): Observable<ReporteClinico> {
    return this.http.get<ReporteClinico>(this.url('reportes/resumen'), {
      params: { desde, hasta },
    });
  }

  registrarPago(request: {
    distribuciones: { idTratamiento: number; montoAplicado: number }[];
    detalles: { idMetodoPago: number; montoAbonado: number; numeroOperacion?: string }[];
  }): Observable<Pago> {
    return this.http.post<Pago>(this.url('pagos'), request);
  }

  tratamientos(): Observable<Tratamiento[]> {
    return this.http.get<Tratamiento[]>(this.url('tratamientos'));
  }

  actualizarEstadoTratamiento(
    id: number,
    estado: string,
  ): Observable<Tratamiento> {
    return this.http.put<Tratamiento>(this.url(`tratamientos/${id}/estado`), { estado });
  }

  agregarProcedimiento(
    idTratamiento: number,
    idProcedimiento: number,
    cantidad: number,
  ): Observable<Tratamiento> {
    return this.http.post<Tratamiento>(
      this.url(`tratamientos/${idTratamiento}/procedimientos`),
      { idProcedimiento, cantidad },
    );
  }
}
