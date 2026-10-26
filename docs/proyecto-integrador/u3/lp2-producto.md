# LP2 - Producto de Unidad 3

**Este documento es el ejemplo BomERP del docente, no una plantilla obligatoria.** Cada sede (Lima, Juliaca, Tarapoto) y cada grupo dentro de una misma sede estabiliza y sustenta su propia base full-stack sobre su propio dominio. Los módulos y casos de prueba concretos de este documento (`catalogo`/`ventas`) son los del ejemplo BomERP; cada equipo los reemplaza por los suyos. Lo exigible a todos es la estructura: backend y frontend modular funcionando integrado, seguridad, optimización, caché, observabilidad, paginación, auditoría, pruebas y estabilización.

## Producto

**Base Full-Stack modular de BomERP —una SPA, una aplicación Spring Boot única (módulos verificados con Spring Modulith) y una base Oracle organizada por esquemas funcionales— integrada, optimizada, monitoreada y estabilizada.**

## Componentes finales

| Componente | Evidencia esperada |
|---|---|
| Backend REST modular | Un `bomerp-backend` ejecutable; Catálogo y Ventas funcionales; Inventario, Compras y Seguridad como paquetes agregados solo cuando su sesión les dio contenido; conexión a Oracle, DTO, validaciones y consultas. |
| Frontend SPA modular | Una SPA con `core`, `shared` y funcionalidades; navegación, CRUD, ventas, consultas, reportes, guards e interceptores. |
| Integración | Flujo SPA → aplicación Spring Boot → módulos → esquemas Oracle completo y verificable. |
| Seguridad | Login propio, JWT, roles, rutas protegidas y manejo de 401/403. SSO no es requisito. |
| Límites modulares | Dependencias unidireccionales, repositorios privados y comunicación interna mediante servicios Java, sin Feign. |
| Optimización frontend | Lazy Loading, Code Splitting y evidencia del build optimizado. |
| Caché | Política de navegador y uso justificado de Redis o justificación de no uso. |
| Observabilidad | Logs estructurados, endpoint de salud y monitoreo básico. |
| Paginación | Listado de alto volumen con metadatos, filtros y ordenamiento conservados. |
| Auditoría | Usuario, fecha, acción, entidad afectada y cambio relevante. |
| Pruebas | Casos funcionales, seguridad, integración, end-to-end y regresión. |
| Estabilización | Bitácora de incidencias, prioridad, corrección y prueba de no regresión. |
| Despliegue o ejecución | Configuración por ambientes y guía reproducible para levantar o revisar el sistema. |

## Plan de pruebas final

| Tipo | Caso | Resultado esperado |
|---|---|---|
| Funcional | Registrar operación principal. | Se persiste y se visualiza correctamente. |
| Seguridad | Acceder sin token a ruta protegida. | Acceso bloqueado. |
| Integración | Ejecutar flujo SPA -> API -> Oracle. | Respuesta coherente y datos actualizados. |
| Validación | Enviar datos inválidos. | Error controlado y mensaje claro. |
| Paginación | Consultar un listado de alto volumen y cambiar de página. | Mantiene filtros, ordenamiento y metadatos consistentes. |
| Auditoría | Ejecutar una operación crítica. | Registra actor, fecha, acción y cambio. |
| Observabilidad | Provocar una operación correcta y un error controlado. | Logs correlacionables y estado de salud visible. |
| End-to-end | Ejecutar autenticación, transacción y consulta desde la SPA. | El flujo completo finaliza sin rupturas. |
| Recuperación | Revisar sistema después de restore de BD. | Datos y flujo principal disponibles. |
| Arquitectura modular | Revisar dependencias entre módulos. | Existe un ejecutable, no hay acceso a repositorios ajenos y los módulos se relacionan mediante servicios públicos. |

## Bitácora de estabilización

| Hallazgo | Corrección | Evidencia |
|---|---|---|
| Error de validación incompleta | Se agregó validación frontend/backend. | Caso de prueba actualizado. |
| Consulta lenta | Se revisó plan e índice con BD2. | Evidencia de optimización. |
| Ruta sin protección | Se agregó guard/interceptor. | Prueba de acceso 401/403. |
| Falta de trazabilidad | Se vinculó endpoint con diseño y tabla. | Matriz final actualizada. |
| Listado inestable con volumen | Se incorporó paginación conservando filtros. | Prueba de alto volumen. |
| Error recurrente del flujo | Se corrigió y añadió prueba de regresión. | E2E ejecutada satisfactoriamente. |

## Cierre LP2

LP2 está completo cuando la base de BomERP continúa el dominio de Ciclo 3, mantiene una sola aplicación Spring Boot y una SPA con módulos cohesionados, ejecuta `Venta–DetalleVenta` de extremo a extremo, aplica seguridad, optimización, caché y observabilidad básica, pagina listados de alto volumen, audita operaciones, supera pruebas end-to-end, queda estabilizada y puede ser sustentada por cada integrante. `Compras` queda como módulo delimitado o ampliación del equipo, no como segundo flujo obligatorio. La preparación para producción tiene alcance académico y debe demostrarse con evidencias reproducibles.
