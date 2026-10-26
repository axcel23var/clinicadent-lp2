export interface Paciente {
  id: number;
  dni: string;
  nombres: string;
  apellidos: string;
  telefono?: string;
}

export interface Procedimiento {
  id: number;
  idEspecialidad: number | null;
  nombre: string;
  precio: number;
}

export interface DetalleTratamientoRequest {
  idProcedimiento: number;
  cantidad: number;
}

export interface DetalleTratamiento {
  id: number;
  idProcedimiento: number;
  precioUnitario: number;
  cantidad: number;
  subtotal: number;
}

export interface TratamientoRequest {
  idPaciente: number;
  detalles: DetalleTratamientoRequest[];
}

export interface Tratamiento {
  id: number;
  idPaciente: number;
  fechaCreacion: string;
  estado: string;
  costoTotal: number;
  saldoPendiente: number;
  detalles: DetalleTratamiento[];
}

export interface DetalleTratamientoDraft {
  idProcedimiento: number;
  nombreProcedimiento: string;
  precioUnitario: number;
  cantidad: number;
}
