import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../../../core/services/api.service';
import { Especialidad } from './especialidad.model';

@Injectable({ providedIn: 'root' })
export class EspecialidadService {
  private readonly http = inject(HttpClient);
  private readonly api = inject(ApiService);
  private readonly resource = '/api/v1/especialidades';

  listar(): Observable<Especialidad[]> {
    return this.http.get<Especialidad[]>(this.api.buildUrl(this.resource));
  }

  obtener(id: number): Observable<Especialidad> {
    return this.http.get<Especialidad>(this.api.buildUrl(`${this.resource}/${id}`));
  }

  crear(especialidad: Especialidad): Observable<Especialidad> {
    return this.http.post<Especialidad>(this.api.buildUrl(this.resource), especialidad);
  }

  actualizar(id: number, especialidad: Especialidad): Observable<Especialidad> {
    return this.http.put<Especialidad>(this.api.buildUrl(`${this.resource}/${id}`), especialidad);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(this.api.buildUrl(`${this.resource}/${id}`));
  }
}
