import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
	{
		path: 'auth/callback',
		loadComponent: () =>
			import('./core/auth/auth-callback').then((module) => module.AuthCallback),
	},
	{
		path: 'sin-permiso',
		loadComponent: () =>
			import('./core/auth/access-denied').then((module) => module.AccessDenied),
	},
	{
		path: '',
		canActivate: [authGuard],
		loadComponent: () => import('./core/layout/layout').then((module) => module.Layout),
		children: [
			{
				path: '',
				pathMatch: 'full',
				loadComponent: () => import('./core/inicio/inicio').then((module) => module.Inicio),
			},
			{
				path: 'catalogo/especialidades',
							canActivate: [authGuard],
				loadComponent: () =>
					import('./features/catalogo/especialidad/especialidad-list').then(
						(module) => module.EspecialidadList,
					),
			},
			{
				path: 'catalogo/especialidades/nueva',
							canActivate: [authGuard, roleGuard('ADMINISTRADOR')],
				loadComponent: () =>
					import('./features/catalogo/especialidad/especialidad-form').then(
						(module) => module.EspecialidadForm,
					),
			},
			{
				path: 'catalogo/especialidades/:id/editar',
							canActivate: [authGuard, roleGuard('ADMINISTRADOR')],
				loadComponent: () =>
					import('./features/catalogo/especialidad/especialidad-form').then(
						(module) => module.EspecialidadForm,
					),
			},
			{
				path: 'tratamientos',
							canActivate: [authGuard, roleGuard('ADMINISTRADOR', 'ODONTOLOGO')],
				loadComponent: () =>
					import('./features/tratamientos/tratamiento-list').then(
						(module) => module.TratamientoList,
					),
			},
			{
				path: 'pacientes',
							canActivate: [authGuard, roleGuard('ADMINISTRADOR', 'RECEPCIONISTA')],
				loadComponent: () =>
					import('./features/admision/pacientes/paciente-page').then(
						(module) => module.PacientePage,
					),
			},
			{
				path: 'citas',
							canActivate: [authGuard, roleGuard('ADMINISTRADOR', 'RECEPCIONISTA')],
				loadComponent: () =>
					import('./features/admision/citas/cita-page').then(
						(module) => module.CitaPage,
					),
			},
			{
				path: 'catalogo/procedimientos',
							canActivate: [authGuard],
				loadComponent: () =>
					import('./features/catalogo/procedimientos/procedimiento-page').then(
						(module) => module.ProcedimientoPage,
					),
			},
			{
				path: 'odontologos',
							canActivate: [authGuard, roleGuard('ADMINISTRADOR')],
				loadComponent: () =>
					import('./features/atenciones/odontologos/odontologo-page').then(
						(module) => module.OdontologoPage,
					),
			},
			{
				path: 'atenciones',
							canActivate: [authGuard, roleGuard('ADMINISTRADOR', 'ODONTOLOGO')],
				loadComponent: () =>
					import('./features/atenciones/atenciones/atencion-page').then(
						(module) => module.AtencionPage,
					),
			},
			{
				path: 'pagos',
							canActivate: [authGuard, roleGuard('ADMINISTRADOR', 'CAJERO')],
				loadComponent: () =>
					import('./features/pagos/pago-page').then((module) => module.PagoPage),
			},
			{
				path: 'reportes',
				canActivate: [authGuard, roleGuard('ADMINISTRADOR', 'CAJERO')],
				loadComponent: () =>
					import('./features/reportes/reportes-page').then((module) => module.ReportesPage),
			},
			{ path: '**', redirectTo: '' },
		],
	},
];
