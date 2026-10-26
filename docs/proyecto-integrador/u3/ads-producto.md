# ADS - Producto de Unidad 3

**Este documento es el ejemplo BomERP del docente, no una plantilla obligatoria.** Cada sede (Lima, Juliaca, Tarapoto) y cada grupo dentro de una misma sede cierra su propio diseño técnico sobre su propio dominio, definido desde S2 y consolidado en sus propios productos U1/U2. Los ADR y la matriz de trazabilidad de este documento son los del ejemplo BomERP; cada equipo los reemplaza por los suyos, con sus propios códigos si ya vienen de U1/U2. Lo exigible a todos es la estructura: arquitectura final, modelo de dominio, catálogo UML, patrones, ADR y trazabilidad completa.

## Producto

**Diseño Técnico Profesional Documentado.**

## Componentes finales

| Componente | Evidencia esperada |
|---|---|
| Arquitectura final | C4 actualizado: contexto, contenedores, componentes y despliegue si aplica. |
| Modelo de dominio | Entidades, reglas, módulos y límites del sistema. |
| Catálogo UML | Clases, secuencia, actividad y estados si aplica. |
| Patrones | Controller, Service, Repository, DTO, Mapper u otros aplicados; Domain-Driven Design táctico (agregado) en los módulos donde se justificó por invariantes reales (candidato: `ventas`, ver Producto ADS U2). |
| ADRs | Decisiones arquitectónicas finales con contexto y consecuencia. |
| Trazabilidad | Relación diseño-BD2-LP2-pruebas. |

## Matriz final de trazabilidad

| Decisión o modelo ADS | Evidencia BD2 | Evidencia LP2 | Prueba o sustentación |
|---|---|---|---|
| API REST stateless con JWT | Usuario/rol de aplicación | Backend y SPA protegidos | Login, guard e interceptor. |
| Auditabilidad de ventas | Trigger o auditoría Oracle | Registro/anulación de venta | Evidencia de evento auditado. |
| Rendimiento en consultas | Índice, estadísticas o AWR | Filtros y consultas API | Comparación de plan o tiempo. |
| Service + Repository | Paquetes/tablas Oracle | Servicios y repositorios | Demo de flujo transaccional. |
| Paginación de alto volumen | Índices y consulta estable | Metadatos, filtros y ordenamiento | Prueba con volumen justificable. |
| Observabilidad | Sesiones, bloqueos y monitoreo Oracle | Logs, salud y monitoreo básico | Diagnóstico de una operación y un error. |
| Integración full-stack | Dato persistido y auditado | Flujo SPA -> API -> Oracle | Prueba end-to-end y regresión. |

## ADR final de referencia

Los códigos de ADR no se reinventan por unidad: son los mismos a lo largo del ciclo. Los tres primeros ya son ADR reales del código (`docs/lp2/adr/`), verificados desde U1; los siguientes se formalizan cuando su sesión llega, no antes (ver [Producto ADS U1](../u1/ads-producto.md), sección 7):

| Código | Decisión | Estado | Evidencia |
|---|---|---|---|
| [ADR-001](../../lp2/adr/ADR-001-arquitectura-backend.md) | Un único proyecto Spring Boot, sin reactor Maven multi-módulo. | Aprobada (U1) | `mvnw test` verde desde S1. |
| [ADR-002](../../lp2/adr/ADR-002-spring-modulith.md) | Módulos de negocio verificados con Spring Modulith. | Aprobada (U1) | `ModularityTests` en verde. |
| [ADR-003](../../lp2/adr/ADR-003-spring-boot-4.md) | Spring Boot exacto en 4.0.7. | Aprobada (U1) | Verificado contra `start.spring.io`. |
| ADR-004 | API REST protegida con JWT. | Formalizada en S10 | Backend y SPA usan token. |
| ADR-005 | Una sola SPA organizada por funcionalidades. | Formalizada en S7 | `core`/`shared`/módulos con rutas propias. |
| ADR-006 | Documentación MkDocs como evidencia técnica navegable. | Formalizada al publicar el sitio | Publicación y repositorio. |
| ADR-007 | Caché y Redis se aplican solo cuando existe beneficio medible. | Formalizada en S13 | Evidencia de caché o decisión documentada de no usarlo. |

## Cierre ADS

El diseño final se considera completo cuando otra persona puede entender la arquitectura, ubicar componentes, relacionar decisiones con evidencias y explicar por qué la solución técnica es coherente con el producto implementado.
