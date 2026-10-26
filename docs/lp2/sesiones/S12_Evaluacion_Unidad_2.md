# S12 - Evaluación de la Unidad II

## 1. Propósito de la evaluación

Esta sesión no enseña contenido nuevo: cierra la Unidad II de **LP2**. El sílabo (sesión 12) define dos actividades:

1. Resolver la evaluación teórico-práctica de los temas de la Unidad II (sesiones 7 a 11).
2. Presentar y sustentar el Frontend SPA seguro integrado al backend.

**Esta sesión coincide con el "Segundo corte integrado" de los tres cursos**, según el cronograma del Proyecto Integrador: ADS evalúa su catálogo UML esta misma semana, en su propia sesión 12; BD2 evalúa su base de datos administrada esta misma semana, en su propia sesión 12. La sustentación de hoy es individual de LP2 — tu SPA, tu código, tu evidencia —, pero la sección 4 exige además evidencia de que esa SPA funciona sobre el backend real y la base administrada de BD2, no aislada.

**Sobre la seguridad evaluada: Keycloak, no JWT propio.** S10 construyó un módulo `seguridad` con JWT propio, deliberadamente temporal ([ADR-005](../adr/ADR-005-seguridad-jwt-propio-listo-para-reemplazo.md)). [S10b](S10b_Seguridad_Backend_Keycloak.md) — de ejecución obligatoria, no opcional — reemplazó ese módulo por Keycloak antes de S11. Lo que esta sesión evalúa es ese estado real: `bomerp-backend` validando tokens de Keycloak, y la SPA iniciando sesión con *Authorization Code* + PKCE — no el JWT hecho a mano que S10 construyó como primer paso pedagógico.

## 2. Producto evaluado

Del sílabo, el producto de la Unidad II es:

> Frontend SPA empresarial seguro, conectado al backend, con navegación modular, CRUD de tablas independientes y dependientes, formularios transaccionales, consultas, reportes y control de acceso.

El producto completo — plantilla-ejemplo con el contenido de BomERP — vive en [`lp2-demo.md`](../../proyecto-integrador/u2/lp2-demo.md): organización funcional, evidencias exigidas, casos de prueba, integración esperada con Keycloak, rúbrica de evaluación y trazabilidad de esa rúbrica contra la malla curricular. La estructura es exigible a todos; el contenido de BomERP se reemplaza por el del propio proyecto de cada equipo.

### Lo que acumulaste sesión por sesión

Este producto no se construye en S12: se ensambla con lo que cada sesión anterior ya te pidió sobre tu propia SPA.

**Tabla 1. De la sesión a la SPA evaluada**

| Sesión | Qué produjiste (tu propio proyecto) | Dónde queda en `lp2-demo.md` |
|---|---|---|
| S7 | Proyecto Angular con navegación principal (`core`/`shared`/`features`), ruteo funcional y un CRUD completo de tu primera tabla independiente, conectado al backend real. | 1. Organización funcional y 3. Evidencias (Navegación modular, CRUD independiente) |
| S8 | CRUD completo de una tabla dependiente, con lista desplegable de datos relacionados y validación de la dependencia. | 3. Evidencias (CRUD dependiente) |
| S9 | Formulario cabecera-detalle real (líneas dinámicas, cálculos en vivo, validación), y una vista de consulta/reporte con filtros. | 3. Evidencias (Formulario cabecera-detalle, Consultas y reportes) |
| S10 | Módulo `seguridad` con JWT propio, roles y endpoints protegidos — el primer paso pedagógico, antes del reemplazo real. | Referencia histórica (nota al inicio de `lp2-demo.md`) |
| S10b | Reemplazo real de `seguridad` por Keycloak: `bomerp-backend` validando tokens del realm propio, sin JWT hecho a mano. | 1. Organización funcional (`core/auth`) y 5. Integración esperada |
| S11 | Login real de tu SPA contra Keycloak (*Authorization Code* + PKCE), token en memoria con copia espejo en `sessionStorage` (sobrevive un `F5`), *guards*, interceptor con refresco en `401`, menú por rol y cierre de sesión real. | 3. Evidencias (Login, Guard, Interceptor, Control de acceso) |
| S12 (esta sesión) | Ensamblas todo lo anterior en una SPA única y segura, y lo sustentas. | El producto completo + sección 4 de esta guía |

Lo que sustentas en S12 es **tu SPA**: los módulos que tú construiste, con los datos y reglas de tu propio dominio, autenticados contra tu propio realm de Keycloak — no el de BomERP. `lp2-demo.md` muestra cómo se ve esa SPA terminada usando el ejemplo del docente; tu entregable real tiene la misma estructura, con el contenido que tú construiste en S7-S11.

## 3. Evaluación teórico-práctica (S7-S11)

Cubre los cinco temas dictados antes de esta sesión. El docente puede tomarla escrita, oral o mixta.

**Tabla 2. Temario de la evaluación teórico-práctica**

| Sesión | Tema | Qué puede evaluar el docente |
|---|---|---|
| S7 | Creación y arquitectura de la SPA | Creación del proyecto frontend, organización `core`/`shared`/`features`, por qué la navegación vive en una ruta padre con `router-outlet`, el servicio HTTP hacia el backend, el interceptor `X-Trace-ID`, el CRUD completo de una tabla independiente, y la diferencia entre un componente de *layout* y uno de funcionalidad. |
| S8 | CRUD de tablas dependientes | Carga de datos relacionados para una lista desplegable, *autocomplete* para elegir entre muchas opciones, validación de la dependencia antes de enviar, presentación de información relacionada, y por qué separar la lista del formulario (`CategoriaList`/`CategoriaForm`) en vez de un formulario escondido en la tabla. |
| S9 | Formularios transaccionales con cabecera-detalle | `FormArray` de líneas, cálculo en vivo de subtotales y total, validación de que exista al menos una línea, confirmación antes de una operación irreversible, consultas y reportes agregados, y manejo específico de errores reales del backend (no un mensaje genérico). |
| S10 / S10b | Seguridad backend y su reemplazo por Keycloak | Usuarios con contraseñas *hash* y datos semilla, autenticación *stateless* con JWT, autorización basada en roles (RBAC), observabilidad y diagnóstico de `401`/`403`, por qué `seguridad` se diseñó "listo para reemplazo" desde el inicio (claims estándar, clave pública publicada, validación por configuración), qué cambia y qué no cambia en `catalogo`/`ventas` al migrar a Keycloak, y por qué `vendedorId` pasa de `Long` a `String` (`sub`). |
| S11 | Seguridad frontend con Keycloak | *Authorization Code* + PKCE y por qué una SPA no puede usar *Direct access grants*, dónde vive el token (memoria con copia espejo en `sessionStorage`, nunca en `localStorage`), por qué el token expira en 5 minutos y hace falta refrescar, los *guards* de autenticación y de rol, la diferencia entre un `401` (refrescar) y un `403` (denegar sin reintentar), y por qué el menú por rol no reemplaza la autorización real del backend. |

Preguntas de referencia (el docente puede formular equivalentes):

1. ¿Por qué el *layout* (menú, sidebar, encabezado) vive en un componente propio con su propio `router-outlet`, en vez de repetirse en cada pantalla?
2. ¿Por qué `ProductoForm` carga sus categorías desde `CategoriaService` en vez de tener la lista escrita a mano en el componente?
3. En tu formulario cabecera-detalle, ¿qué validación impide enviar una venta sin ninguna línea?
4. ¿Por qué `vendedorId` se lee del claim `sub` del token ya verificado, y nunca de un campo que el cliente podría enviar en el `request`?
5. ¿Qué tendría que cambiar en `catalogo` o `ventas` si tu backend reemplazara Keycloak por otro proveedor de identidad? ¿Por qué esa lista es tan corta?
6. ¿Por qué el `code_verifier` de PKCE se guarda en `sessionStorage`, si el resto de la sesión vive en memoria?
7. ¿Qué pierde tu aplicación al elegir memoria en vez de `localStorage` para el token, y por qué esa pérdida es aceptable?
8. Si tu interceptor reintentara una petición en un bucle sin límite cada vez que recibiera un `401`, ¿qué problema real esconderías?

## 4. Sustentación de la SPA segura

**Tabla 3. Distribución de tiempo de la sustentación**

| Momento | Tiempo | Quién | Propósito |
|---|---:|---|---|
| Alcance del proyecto (diapositiva) | 2-3 min | Grupal | **Una sola vez para los tres cursos** (si ADS, BD2 y LP2 se sustentan juntos) — qué hace el sistema, para quién, y su relación con el Proyecto Integrador. No se repite en ADS/BD2. |
| Arquitectura (diapositiva) | 2-3 min | Grupal | Organización de la SPA (`core`/`shared`/`features`, Keycloak) y el diagrama de integración SPA → backend → Oracle, sobre el propio dominio del equipo. |
| Asignación de tareas (diapositiva) | 2-3 min | Grupal | Asignación por módulo: cada integrante presenta al menos un módulo transaccional propio (cabecera-detalle, con una relación muchos a muchos resuelta por la propia tabla de detalle) que construyó. |
| Demo y sustentación | 3 min por integrante | Individual | Cada integrante ejecuta en vivo y sustenta su propio módulo transaccional: CRUD, cálculos, validaciones. |
| Preguntas | 3 min por integrante | Individual | Verificar dominio y aporte propio, con base en la Tabla 2. |

**Tabla 4. Entregables obligatorios**

| Entregable | Evidencia mínima | Criterio de aceptación |
|---|---|---|
| Producto de unidad | [`lp2-demo.md`](../../proyecto-integrador/u2/lp2-demo.md) completo | Coherente con el sílabo y con el código real ejecutable |
| Evidencia de integración | SPA conectada al backend real, backend validando Keycloak, esquemas Oracle administrados de BD2 | Trazabilidad verificable con ADS y BD2, no solo documentada |
| Sustentación individual | Preguntas y defensa por integrante (sección 3) | Autoría demostrada |

Secuencia sugerida de presentación (referencias a secciones de `lp2-demo.md`):

1. Presentar la organización funcional de la SPA y su navegación modular.
2. Ejecutar en vivo el CRUD dependiente, con la validación de la dependencia provocada a propósito.
3. Ejecutar el formulario cabecera-detalle: un caso de éxito y un caso de error real del backend (producto inexistente o stock insuficiente).
4. Mostrar el login real contra Keycloak y el token guardado en memoria con copia espejo en `sessionStorage` (nunca en `localStorage`); recargar la página (`F5`) y mostrar que la sesión sobrevive.
5. Provocar un `401` (esperar la expiración) y mostrar el refresco automático; provocar un `403` con un rol insuficiente y mostrar que no reintenta.
6. Mostrar el menú y una acción protegida (ej. "Anular") visibles solo para el rol correcto.
7. Cerrar con la tabla de integración esperada de `lp2-demo.md`, explicando al menos una fila con evidencia en vivo desde el backend y la base de BD2.

Criterios mínimos de aceptación:

- La SPA arranca y conecta con el backend real, no con datos simulados.
- El CRUD dependiente valida la relación antes de enviar, con al menos un caso inválido probado.
- El formulario cabecera-detalle calcula en vivo y maneja al menos un error real del backend.
- El login es real contra Keycloak (*Authorization Code* + PKCE), sin ningún formulario propio de login.
- El token vive en memoria con copia espejo en `sessionStorage` (nunca en `localStorage`), sobreviviendo un `F5`; el refresco ante `401` y la denegación ante `403` están verificados en vivo.
- El menú y al menos una acción protegida reflejan exactamente las reglas de autorización reales del backend.
- Cada integrante responde individualmente al menos una pregunta de la Tabla 2.

## 5. Rúbrica de evaluación

La rúbrica (6 criterios: 5 cita literal de los criterios de evaluación del producto de la Unidad II + sustentación) vive en [`lp2-demo.md`](../../proyecto-integrador/u2/lp2-demo.md#6-rubrica-de-evaluacion), junto con la plantilla del producto y su trazabilidad con la malla curricular (CE023 Nivel 2, parcial). Úsala directamente desde ahí para calificar la sustentación de esta sesión — no se duplica aquí.
