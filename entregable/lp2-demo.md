# LP2 - Producto de Unidad 2: ClinicaDent

**Proyecto:** ClinicaDent — Sistema de Gestión Clínica para Centro Odontológico  
**Equipo:** InnovateX  
**Sede:** Juliaca
**Curso:** Lenguaje de Programación II (LP2) — Segundo Corte Integrado  

### Asignación de Módulos y Responsabilidades
| Integrante | Módulo Asignado | Alcance Funcional | Tipo de Módulo Transaccional |
|---|---|---|---|
| **Eddy Brayan Huaman Taco** | Módulo 1: Citas y Admisión | Padrón de Pacientes y Agendamiento de Citas con procedimientos solicitados. | Cabecera–Detalle: `Cita` ◄► `DetalleCita` ◄► `Procedimiento` |
| **Paul Cariapaza Sacachipana** | Módulo 2: Catálogo y Tratamientos | Catálogo de Especialidades, Tarifario de Procedimientos y Planes de Tratamiento Presupuestados. | Cabecera–Detalle: `Tratamiento` ◄► `DetalleTratamiento` ◄► `Procedimiento` |
| **Erick Midwar Calsin Chura** | Módulo 3: Atenciones Clínicas | Registro de Odontólogos y Atención Clínica por pieza dental con cambio de estado de cita. | Cabecera–Detalle: `Atencion` ◄► `DetalleAtencion` ◄► `Procedimiento` |
| **Axcel Daniel Vargas Quispe** | Módulo 4: Pagos y Caja | Métodos de Pago, Operación de Caja con conciliación y Reportes de Recaudación. | Doble N:M: `Pago` ◄► `DetallePago` ◄► `MetodoPago` y `Pago` ◄► `DistribucionPago` ◄► `Tratamiento` |

---

## Declaración del Producto Evaluado

**Frontend SPA empresarial modular y seguro en Angular 19, conectado al backend modular Spring Boot (`pe.edu.upeu.clinica`), autenticado contra Keycloak (OpenID Connect / OAuth2 mediante *Authorization Code* + PKCE) y con persistencia transaccional en Oracle Database.**

El sistema automatiza el ciclo de vida clínico integral de un centro odontológico bajo una arquitectura orientada al dominio:
$$\text{Paciente} \longrightarrow \text{Cita Programada} \longrightarrow \text{Plan de Tratamiento} \longrightarrow \text{Atención Clínica} \longrightarrow \text{Conciliación en Caja y Pago}$$

Toda la seguridad se centraliza en **Keycloak** (realm `clinicadent`), descartando cualquier autenticación artesanal o almacenamiento inseguro de credenciales. La SPA interactúa con el backend utilizando tokens JWT Bearer, con control de acceso basado en roles (**RBAC**: `ADMINISTRADOR`, `RECEPCIONISTA`, `ODONTOLOGO`, `CAJERO`), *guards* de ruta, interceptores HTTP con refresco reactivo ante errores `401` y denegación estricta ante `403`.

---

## 1. Organización Funcional de la SPA

La estructura del frontend (`frontend/src/app/`) separa rigurosamente las responsabilidades de infraestructura transversal (`core`), elementos compartidos (`shared`) y funcionalidades de negocio (`features`), operando sobre un shell único con navegación modular:

```text
frontend/src/app/
├── core/
│   ├── auth/                       # Integración con Keycloak (PKCE, estado de sesión reactivo con Signals)
│   │   ├── access-denied.ts        # Vista de advertencia para acceso denegado (403)
│   │   ├── auth-callback.ts        # Receptor de redirección OpenID Connect
│   │   └── auth.service.ts         # Inicialización de Keycloak, roles, token y cierre de sesión
│   ├── guards/
│   │   ├── auth.guard.ts           # Protección de rutas contra usuarios anónimos (redirección a Keycloak)
│   │   └── role.guard.ts           # Control de acceso basado en roles de realm (RBAC)
│   ├── inicio/                     # Dashboard de bienvenida con accesos rápidos y estado del sistema
│   │   ├── inicio.html
│   │   └── inicio.ts
│   ├── interceptors/
│   │   ├── auth.interceptor.ts     # Inyección de Bearer Token y refresco automático ante HTTP 401
│   │   └── trace-id.interceptor.ts # Inyección de cabecera de observabilidad X-Trace-ID
│   ├── layout/                     # Shell principal (Topbar corporativo, Sidebar con menú por rol y RouterOutlet)
│   │   ├── layout.html
│   │   └── layout.ts
│   └── services/
│       ├── api.service.ts          # Configuración base de URLs y cliente HTTP
│       └── clinic-api.service.ts   # Gateway de consumo de endpoints REST de ClinicaDent
├── shared/
│   └── transaction-workflow.css    # Estilos compartidos para formularios cabecera-detalle y paneles
└── features/
    ├── admision/                   # MÓDULO 1: Admisión y Citas
    │   ├── pacientes/              # CRUD independiente del padrón de pacientes (DNI, nombres, teléfono)
    │   │   ├── paciente-page.html
    │   │   └── paciente-page.ts
    │   └── citas/                  # Formulario transaccional de citas médicas (Cabecera-Detalle)
    │       ├── cita-page.html
    │       └── cita-page.ts
    ├── catalogo/                   # MÓDULO 2: Catálogo de Servicios
    │   ├── especialidad/           # CRUD independiente de Especialidades odontológicas
    │   │   ├── especialidad-form.html / .ts
    │   │   ├── especialidad-list.html / .ts
    │   │   └── especialidad.service.ts
    │   └── procedimientos/         # CRUD dependiente de Procedimientos (Tarifario vinculado a Especialidad)
    │       ├── procedimiento-page.html
    │       └── procedimiento-page.ts
    ├── tratamientos/               # MÓDULO 2: Presupuestos y Planes Clínicos
    │   ├── tratamiento-list.html   # Formulario cabecera-detalle con cálculos en vivo y cambio de estados
    │   ├── tratamiento-list.ts
    │   └── tratamiento.service.ts
    ├── atenciones/                 # MÓDULO 3: Atenciones Clínicas Odontológicas
    │   ├── odontologos/            # Mantenimiento de personal médico (COP, nombres, especialidad)
    │   │   ├── odontologo-page.html
    │   │   └── odontologo-page.ts
    │   └── atenciones/             # Formulario transaccional clínico por piezas dentales (FDI)
    │       ├── atencion-page.html
    │       └── atencion-page.ts
    ├── pagos/                      # MÓDULO 4: Gestión de Caja y Recaudación
    │   ├── pago-page.html          # Formulario transaccional de abonos con doble N:M y conciliación
    │   └── pago-page.ts
    └── reportes/                   # Reportes de Gestión e Indicadores
        ├── reportes-page.html      # Consulta analítica de recaudación y prestaciones por rango de fechas
        └── reportes-page.ts
```

---

## 2. Demo Ejecutable y Puntos de Acceso Local

El sistema opera de forma integrada en el entorno de desarrollo local:

| Componente | Dirección / URL | Descripción Técnica |
|---|---|---|
| **Frontend SPA (Angular)** | `http://localhost:4200` | Interfaz reactiva empresarial con control de acceso por rol. |
| **Backend REST (Spring Boot)** | `http://localhost:8080` | API REST con Spring Security Resource Server (`/api/v1/...`). |
| **Swagger UI / OpenAPI** | `http://localhost:8080/swagger-ui.html` | Catálogo interactivo de documentación de endpoints. |
| **Servidor Keycloak** | `http://localhost:38081` | Servidor de identidad OpenID Connect (Realm: `clinicadent`). |
| **Base de Datos Keycloak** | `localhost:5432` (`postgres:17-alpine`) | Persistencia interna de realms, roles y usuarios de Keycloak. |
| **Base de Datos Clínica** | `localhost:1521` / `1522` (`FREEPDB1`) | Oracle Database 23ai Free con esquema funcional `CLINICADENT_APP`. |

---

## 3. Evidencias Exigidas al Producto Real U2

**Tabla 1. Evidencias exigidas al producto real U2 (ClinicaDent)**

| Elemento | Componente(s) en ClinicaDent | Qué demuestra en la sustentación |
|---|---|---|
| **Login Seguro** | `AuthService` (`core/auth/auth.service.ts`), `keycloak-js` | Autenticación real contra Keycloak mediante *Authorization Code* con PKCE (S256). Ninguna credencial pasa por el frontend; la sesión se conserva en memoria con soporte de refresco. |
| **Guards de Ruta** | `authGuard` (`auth.guard.ts`), `roleGuard` (`role.guard.ts`) | Bloqueo estricto de rutas a usuarios no autenticados (`authGuard`). Protección modular por rol (`roleGuard`): `/odontologos` solo para `ADMINISTRADOR`; `/pagos` y `/reportes` para `CAJERO` y `ADMINISTRADOR`; `/tratamientos` y `/atenciones` para `ODONTOLOGO` y `ADMINISTRADOR`. Redirección a `/sin-permiso` en caso de denegación. |
| **Interceptor HTTP** | `authInterceptor` (`core/interceptors/auth.interceptor.ts`) | Adjunta automáticamente la cabecera `Authorization: Bearer <token>` a todas las peticiones salientes dirigidas a `environment.apiBaseUrl`. Ante un `HTTP 401`, ejecuta reactivamente `auth.refreshToken()` y reintenta la solicitud fallida una única vez; si el error es `403` o el token expiró irrevocablemente, aborta sin bucles infinitos. |
| **Navegación Modular** | `Layout` (`core/layout/layout.ts`, `layout.html`) | Shell corporativo con sidebar dinámico. Los ítems de navegación se filtran condicionalmente según los roles del usuario autenticado (`auth.hasAnyRole(...)`), impidiendo la visualización de módulos no autorizados. |
| **CRUD Independiente** | `PacientePage` (`features/admision/pacientes/`) y `EspecialidadForm` (`features/catalogo/especialidad/`) | Gestión integral (crear, listar, editar, eliminar) de entidades maestras sin llaves foráneas dependientes. Validación de unicidad de DNI en `Paciente` (código HTTP 409) y validación de nombres de especialidad. |
| **CRUD Dependiente** | `ProcedimientoPage` (`features/catalogo/procedimientos/`) | Tarifario clínico dependiente de `Especialidad`. Carga un menú desplegable `<select>` desde `api.especialidades()`. El backend ([ProcedimientoServiceImpl.java](file:///d:/clinica-lp2/clinica-lp2/CLINICADENT-backend/src/main/java/pe/edu/upeu/clinica/tratamientos/catalogo/service/impl/ProcedimientoServiceImpl.java)) valida en servidor que `idEspecialidad` exista en BD antes de persistir, lanzando `ResourceNotFoundException` si es inválida. |
| **Formulario Cabecera–Detalle** | `PagoPage` (`features/pagos/`) y `TratamientoList` (`features/tratamientos/`) | **Pagos:** Formulario complejo con doble N:M (Líneas de métodos de pago + líneas de distribución entre tratamientos), validación de saldos y cálculo de conciliación en vivo. **Tratamientos:** Gestión de procedimientos solicitados con cálculo dinámico de subtotales, costo total y control de transiciones de estado (`BORRADOR` → `APROBADO` → `EN_PROCESO` → `FINALIZADO`). |
| **Consultas y Reportes** | `ReportesPage` (`features/reportes/`) | Panel de control de recaudación con filtros por fecha (`desde`, `hasta`), desglose de ingresos por método de pago, cantidad de prestaciones realizadas y gráfico de barras proporcional en CSS puro. |
| **Control de Acceso (RBAC)** | `SecurityConfig.java` en Backend y `Layout` en Frontend | Alineación completa de la matriz de autorización: las restricciones visuales de la SPA se reflejan exactamente en los `requestMatchers` de Spring Security, garantizando que una petición interceptada sin el rol adecuado reciba `HTTP 403 Forbidden`. |

---

## 4. Casos de Prueba de la Demo U2

**Tabla 2. Casos de prueba de la demo ClinicaDent (Unidad 2)**

| Caso | Acción a Ejecutar | Resultado Esperado en ClinicaDent |
|---|---|---|
| **1. Acceso sin sesión** | Intentar navegar directamente a `http://localhost:4200/citas` o `/pagos` con sesión cerrada. | El `authGuard` intercepta la navegación, almacena la URL de retorno en `sessionStorage` y redirige a la pantalla de login de Keycloak (`http://localhost:38081`). |
| **2. Login exitoso** | Ingresar usuario y contraseña asignados en el realm `clinicadent` (ej. `admin_clinica` / `admin123`). | Redirección de retorno exitosa a `/auth/callback`, el sidebar se activa mostrando el nombre de usuario y los módulos correspondientes a sus roles. |
| **3. CRUD independiente** | Crear un paciente con DNI `74859612`, editar su teléfono a `987654321` y listar el registro en `/pacientes`. | El registro se almacena en la tabla `PACIENTES` de Oracle y se visualiza de inmediato en la tabla de la SPA con feedback positivo. |
| **4. CRUD dependiente** | En `/catalogo/procedimientos`, seleccionar la especialidad `Ortodoncia`, ingresar nombre `Instalación de Brackets` y precio `1500.00`. | El procedimiento se asocia correctamente al `ID_ESPECIALIDAD`. Si se envía un ID foráneo inexistente, el backend rechaza con `404 Not Found`. |
| **5. Cita y Atención clínica (Cabecera-Detalle)** | Agendar una cita con procedimiento `Profilaxis` en `/citas`. Luego, en `/atenciones`, registrar el diagnóstico médico e intervenir la pieza `11`. | Se crea la atención con sus líneas de detalle en `DETALLE_ATENCIONES`. El backend invoca a `citaEstadoPort.actualizarEstado()` y marca la cita automáticamente como `ATENDIDA`. |
| **6. Abono de Caja conciliado** | En `/pagos`, seleccionar un tratamiento con saldo de S/ 1,500.00, aplicar S/ 600.00 y registrar el pago con el método `Yape`. | Se registran en una sola transacción `PAGO`, `DETALLE_PAGOS` y `DISTRIBUCION_PAGOS`. El saldo del tratamiento disminuye automáticamente a S/ 900.00 y su estado cambia a `EN_PROCESO`. |
| **7. Error de validación y Rollback** | En `/pagos`, intentar aplicar un abono de S/ 2,000.00 a un tratamiento con saldo de S/ 1,500.00, o desfasar la suma de métodos de pago. | El frontend bloquea la operación indicando que el monto supera el saldo. Si se fuerza al backend, este lanza `MontoExcedidoException` (HTTP 400), aplicando rollback atómico sin registros huérfanos. |
| **8. Permiso insuficiente (403)** | Iniciar sesión como `RECEPCIONISTA` e intentar entrar manualmente a `http://localhost:4200/odontologos` o `/pagos`. | El `roleGuard` deniega el acceso y redirige a `/sin-permiso`. Si se invoca el endpoint REST por HTTP, el backend retorna `403 Forbidden`. |

---

## 5. Integración Esperada del Sistema

La solución opera con desacoplamiento total entre capas, conectando el cliente web empresarial con el ecosistema de servicios corporativos:

```mermaid
flowchart LR
    subgraph CLIENTE["Frontend SPA (Angular 19)"]
        SPA["ClinicaDent Web App<br/>Port 4200<br/>core · shared · features"]
    end

    subgraph SEGURIDAD["Identidad & Acceso (Keycloak 26)"]
        KC["Keycloak Server<br/>Port 38081<br/>Realm: clinicadent"]
        KCDB[("PostgreSQL 17<br/>Port 5432<br/>clinicadent_keycloak")]
    end

    subgraph BACKEND["Backend REST (Spring Boot 3.4)"]
        API["ClinicaDent Backend<br/>Port 8080<br/>pe.edu.upeu.clinica"]
        MOD1["citas · admision"]
        MOD2["tratamientos · catalogo"]
        MOD3["atenciones"]
        MOD4["pagos · reportes"]
    end

    subgraph PERSISTENCIA["Base de Datos Empresarial"]
        DB[("Oracle Database 23ai Free<br/>Port 1521/1522<br/>Esquema: CLINICADENT_APP")]
    end

    SPA -->|1. Auth Code + PKCE (S256)| KC
    KC --> KCDB
    SPA -->|2. HTTP Requests + JWT Bearer Token| API
    API -->|3. Valida JWT con Issuer URI| KC
    API --> MOD1 & MOD2 & MOD3 & MOD4
    MOD1 & MOD2 & MOD3 & MOD4 -->|4. JPA / Hibernate ORM| DB
```

---

## 6. Rúbrica de Evaluación Oficial (Unidad 2)

**Tabla 3. Rúbrica de evaluación de la Unidad 2 (Cita oficial)**

| Criterio | Peso | CE / Nivel | A (20 pts) | B (15 pts) | C (10 pts) | D (5 pts) | Calificación obtenida |
|---|---:|---|---|---|---|---|---:|
| 1. Crea la SPA con proyecto frontend, layout, menú bar, sidebar, encabezado, módulos, componentes, rutas, navegación, servicios HTTP y CRUD de una tabla independiente | 16% | CE023-N2 (parcial) | SPA navegable, conectada al backend real, con CRUD independiente funcional de punta a punta. | SPA navegable y conectada, con el CRUD independiente parcialmente funcional. | SPA creada, sin conexión real al backend o sin CRUD funcional. | No presenta una SPA ejecutable. | |
| 2. Implementa CRUD de tablas dependientes mediante selección de datos relacionados, listas desplegables y validación de dependencias | 16% | CE023-N2 (parcial) | CRUD dependiente completo, con listas desplegables y validación de dependencias verificada en vivo. | CRUD dependiente funcional, con validación de dependencias incompleta. | CRUD dependiente presente, sin selección real de datos relacionados. | No presenta CRUD de tablas dependientes. | |
| 3. Implementa formularios transaccionales con cabecera–detalle, cálculos, validaciones, confirmación, consultas y reportes | 16% | CE023-N2 (parcial) | Formulario cabecera-detalle completo, con cálculos, validaciones, confirmación, y al menos una consulta o reporte agregado funcionando. | Formulario completo, con la consulta o el reporte incompleto. | Formulario incompleto o sin cálculos/validaciones reales. | No presenta formulario cabecera-detalle. | |
| 4. Reemplaza la seguridad backend por un proveedor de identidad real (Keycloak), con roles y permisos efectivos | 16% | CE023-N2 (parcial) | `bomerp-backend` (o backend del equipo) valida tokens de Keycloak (no JWT propio), con roles reales protegiendo endpoints, verificado con al menos dos usuarios de rol distinto. | Backend validando Keycloak, con la matriz de roles incompleta o sin verificar con más de un usuario. | Backend con seguridad propia (JWT hecho a mano) sin avanzar al proveedor externo. | No presenta ningún mecanismo de autorización por rol en el backend. | |
| 5. Implementa login, sesión, almacenamiento y expiración del token, guards, interceptores, menú por permisos, rutas protegidas y manejo de respuestas 401/403 | 16% | CE023-N2 (parcial) | Login real contra Keycloak (*Authorization Code* + PKCE), token en memoria con copia espejo en `sessionStorage` (sobrevive un `F5`), *guards* e interceptor funcionando, con refresco ante `401` y denegación clara ante `403`, verificado con la matriz de accesos completa. | Flujo de login y *guards* funcionando, con el interceptor o el refresco de token incompleto. | Login funcional con un flujo inseguro (ej. token en `localStorage`, o sin PKCE). | No presenta login ni protección de rutas en el frontend. | |
| 6. Sustentación | 20% | CG | Sustenta con claridad y profesionalismo su aporte individual, respondiendo con precisión las preguntas del jurado. | Sustenta con solvencia, con detalles menores en claridad, orden o precisión. | Sustenta con dificultad; claridad, orden o precisión insuficientes. | No sustenta adecuadamente ni demuestra su aporte individual. | |

Nota final = suma de (`Peso` × `Puntos de la calificación obtenida`) / 100 × 20.

---

## 7. Subaspectos de la Sustentación Individual (Unidad 2)

**Tabla 4. Subaspectos de la sustentación (Unidad 2)**

| Subaspecto | Qué observa el jurado en la Unidad 2 | Evidencia Demostrada por el Equipo InnovateX |
|---|---|---|
| **1. Aporte individual** | Cada integrante demuestra al menos un módulo transaccional propio (cabecera-detalle, con una relación muchos a muchos resuelta por la propia tabla de detalle) que construyó. | Cada integrante defiende su módulo asignado: Huaman (Citas y Admisión), Cariapaza (Catálogo y Tratamientos), Calsin (Atenciones Clínicas) y Vargas (Pagos y Caja). |
| **2. Comunicación y orden** | Claridad, estructura, tiempo y lenguaje técnico durante la presentación. | Exposición ordenada siguiendo el flujo clínico del paciente y demostrando la trazabilidad full-stack desde el navegador hasta la base Oracle. |
| **3. Presentación personal y actitud** | Puntualidad, vestimenta formal/adecuada, actitud profesional, respeto y coherencia con los valores institucionales. | Presentación corporativa en equipo con roles definidos para cada intervención. |
| **4. Repositorio y estándares** | Topics académicos configurados desde S2, organización de código, commits semánticos y reproducibilidad de la SPA. | Repositorio estructurado en `frontend/` y `CLINICADENT-backend/`, configuración de Docker Compose para Keycloak y Oracle, y scripts reproducibles. |
| **5. MkDocs o equivalente** | Documentación de Unidad 2 publicada, navegable y alineada con la entrega técnica. | Estructura de documentación formal en Markdown para su integración inmediata al sitio de documentación. |
| **6. Pitch/demo ejecutiva** | Introducción breve del sistema y su impacto en la gestión clínica de un centro odontológico con apoyo visual. | Presentación previa de 2 minutos explicando la necesidad operativa de ClinicaDent antes de la ejecución de casos de prueba. |

---

## 8. Trazabilidad con la Malla Curricular

Los criterios 1 al 5 corresponden a la porción de **frontend SPA, seguridad e integración full-stack** del **Nivel 2 de la Competencia Específica CE023 (Programación)**:
* **Unidad 1:** Implementó y validó la capa de persistencia y API REST en Spring Boot con Oracle Database.
* **Unidad 2:** Completa la arquitectura empresarial construyendo la interfaz de usuario reactiva, formularios transaccionales cabecera-detalle, observabilidad y control de acceso robusto mediante Keycloak.
* **Criterio 6 (Sustentación):** Evalúa la **Competencia General (CG) de Innovación y Solución de Problemas**, comprobando el dominio técnico, la autoría y la solvencia en la defensa del código fuente por cada miembro del equipo.
