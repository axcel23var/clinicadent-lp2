import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../../core/services/api.service';
import {
  DetalleTratamientoRequest,
  Paciente,
  Procedimiento,
  Tratamiento,
  TratamientoRequest,
} from './tratamiento.model';

@Injectable({ providedIn: 'root' })
export class TratamientoService {
  private readonly http = inject(HttpClient);
  private readonly api = inject(ApiService);

  listarPacientes(): Observable<Paciente[]> {
    return this.http.get<Paciente[]>(this.api.buildUrl('/api/v1/pacientes'));
  }

  listarProcedimientos(): Observable<Procedimiento[]> {
    return this.http.get<Procedimiento[]>(this.api.buildUrl('/api/v1/procedimientos'));
  }

  listarTratamientos(): Observable<Tratamiento[]> {
    return this.http.get<Tratamiento[]>(this.api.buildUrl('/api/v1/tratamientos'));
  }

  crear(request: TratamientoRequest): Observable<Tratamiento> {
    return this.http.post<Tratamiento>(
      this.api.buildUrl('/api/v1/tratamientos'),
      request,
    );
  }

  agregarProcedimiento(
    tratamientoId: number,
    request: DetalleTratamientoRequest,
  ): Observable<Tratamiento> {
    return this.http.post<Tratamiento>(
      this.api.buildUrl(`/api/v1/tratamientos/${tratamientoId}/procedimientos`),
      request,
    );
  }

  actualizarEstado(tratamientoId: number, estado: string): Observable<Tratamiento> {
    return this.http.put<Tratamiento>(
      this.api.buildUrl(`/api/v1/tratamientos/${tratamientoId}/estado`),
      { estado },
    );
  }
}
