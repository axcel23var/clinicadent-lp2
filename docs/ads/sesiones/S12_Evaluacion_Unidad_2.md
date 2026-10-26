# S12 - Evaluación de la Unidad II

## 1. Propósito de la evaluación

Esta sesión no enseña contenido nuevo: cierra la Unidad II de **ADS**. El sílabo (sesión 12) define dos actividades:

1. Resolver la evaluación teórico-práctica de los temas de la Unidad II (sesiones 6 a 11).
2. Presentar y sustentar el Catálogo UML con patrones de diseño e integración aplicados.

Esta sesión evalúa únicamente el producto de ADS. El "segundo corte integrado" (catálogo UML, base Oracle administrada y SPA segura integrada al backend) se arma recién cuando las tres evaluaciones de unidad —esta, la de BD2 y la de LP2, todas en S11-S12— ya están hechas por separado; no se evalúa la integración cruzada dentro de esta sesión.

## 2. Producto evaluado

Del sílabo, el producto de la Unidad II es:

> Catálogo UML estructural y dinámico con modelo de dominio, clases, diagramas de interacción, patrones aplicados, diseño conceptual de APIs e integración empresarial.

El producto completo — plantilla-ejemplo con el contenido de BomERP — vive en [`ads-producto.md`](../../proyecto-integrador/u2/ads-producto.md): artefactos mínimos, diagrama de clases de referencia, trazabilidad técnica U2, rúbrica de evaluación y trazabilidad de esa rúbrica contra la malla curricular. La estructura es exigible a todos; el contenido de BomERP se reemplaza por el del propio proyecto de cada equipo.

### Lo que acumulaste sesión por sesión

Este producto no se construye en S12: se ensambla con lo que cada sesión anterior ya te pidió sobre tu propio proyecto.

**Tabla 1. De la sesión al documento final**

| Sesión | Qué produjiste (tu propio proyecto) | Dónde queda en `ads-producto.md` |
|---|---|---|
| S6 | Modelo de dominio inicial: subdominios, entidades, reglas de negocio, casos de uso relevantes, objetos de valor, lenguaje ubicuo y al menos un agregado de tu propio proyecto. | 1. Artefactos mínimos (Modelo de dominio) |
| S7 | Diagrama de clases UML del dominio de tu propio proyecto: entidades, atributos, operaciones, multiplicidades, al menos una agregación, una composición y una jerarquía de herencia. | 1. Artefactos mínimos (Diagrama de clases) y 2. Diagrama de clases de referencia |
| S8 | Diseño de clases por capas de un módulo propio (Controller, Service, Repository, Mapper, entidades, DTO), su contrato REST conceptual, el modelo relacional y la matriz de trazabilidad dominio-clase-tabla-DTO. | 1. Artefactos mínimos (Diseño de clases por capas, Transformación objeto-relacional) |
| S9 | Diagramas dinámicos del escenario crítico de tu propio proyecto: secuencia, actividad, interacción entre componentes (C3), comunicación, y la matriz de consistencia frente a las clases de S8. | 1. Artefactos mínimos (Diagramas dinámicos) |
| S10 | Catálogo de patrones GoF y GRASP identificados y aplicados, con cada patrón explicado primero en general y después sobre el código real de tu propio módulo. | 1. Artefactos mínimos (Patrones aplicados) |
| S11 | Diseño de integración empresarial: capa anticorrupción y *Circuit Breaker* protegiendo al menos un sistema externo real, evento de dominio con publicación confiable (*Outbox*), consumidor desacoplado, Saga coreografiada con su paso de compensación, y matriz de integración completa. | 1. Artefactos mínimos (Diseño de integración) |
| S12 (esta sesión) | Ensamblas todo lo anterior en un solo documento y lo sustentas. | El documento completo + sección 4 de esta guía |

Lo que sustentas en S12 es **tu diseño detallado**: el dominio que tú modelaste en S6, las clases y la transformación objeto-relacional de S7-S8, los escenarios dinámicos de S9, los patrones que de verdad encontraste y aplicaste en S10, y la integración que diseñaste en S11 — no la de BomERP.

## 3. Evaluación teórico-práctica (S6-S11)

Cubre los seis temas dictados antes de esta sesión. El docente puede tomarla escrita, oral o mixta.

**Tabla 2. Temario de la evaluación teórico-práctica**

| Sesión | Tema | Qué puede evaluar el docente |
|---|---|---|
| S6 | Descubrimiento y modelado del dominio | Panorama holístico del negocio, clasificación de subdominios (*Core*, *Supporting*, *Generic*), entidades y reglas de negocio por módulo, casos de uso relevantes, objetos de valor, la prueba de tres partes para confirmar límites entre módulos, lenguaje ubicuo y agregado como límite de consistencia. |
| S7 | Diseño de clases del dominio | Entidades persistentes y atributos, operaciones de clase, relaciones y multiplicidades, diferencia real entre agregación y composición, herencia, y restricciones del modelo que no se expresan solo en el diagrama. |
| S8 | Diseño de clases avanzado y transformación objeto-relacional | De clase de dominio a clase de diseño, clases de diseño por capas, DTO/*Mapper*/contrato REST, transformación objeto-relacional (clases, tablas, claves), cómo se mapean asociaciones, herencia y objetos de valor a tablas, y trazabilidad dominio-clases-base de datos. |
| S9 | Diagramas dinámicos UML | Criterio verificable para decidir cuándo hace falta un diagrama dinámico adicional (y cuándo no), diagrama de secuencia, diagrama de actividades, ciclo de vida de objetos (diagrama de estados, cuándo es opcional), interacción entre componentes a nivel C3, y diagrama de comunicación como vista alternativa de la misma colaboración. |
| S10 | Patrones de diseño GoF y GRASP | Patrones creacionales (*Factory Method*, *Builder*, *Singleton*, *Abstract Factory*), estructurales (*Facade*, *Proxy*, *Adapter*, *Bridge*), de comportamiento (*Strategy*, *State*, *Chain of Responsibility*, *Observer*), GRASP de asignación de responsabilidades (*Information Expert*, *Creator*, *Controller*), de acoplamiento/cohesión (*Low Coupling*, *High Cohesion*, *Polymorphism*), de protección (*Pure Fabrication*, *Indirection*, *Protected Variations*) y *Dependency Injection*. |
| S11 | Integración y sistemas empresariales | APIs externas y servicios de terceros, eventos de dominio y publicación/suscripción, integración síncrona frente a asíncrona, servicios de inteligencia artificial, capa anticorrupción, *Circuit Breaker* (tasa de fallos sobre ventana deslizante, no fallos consecutivos), *Outbox*, y Saga coreografiada con su compensación frente al *fallback* de un *Circuit Breaker*. |

Preguntas de referencia (el docente puede formular equivalentes):

1. ¿Por qué un diagrama de clases, por sí solo, no alcanza para explicar cómo colabora tu propio escenario crítico? ¿Qué pregunta responde un diagrama dinámico que el diagrama de clases no responde?
2. Un equipo modela `Cliente` y `Venta` como asociación simple, sin agregación ni composición. ¿Qué pregunta de la prueba de tres partes (S6) debería haberles hecho dudar de ese límite?
3. ¿Por qué `Venta.anular()` vive en la entidad `Venta` y no en un `VentaService` externo? Relaciona tu respuesta con *Information Expert* (GRASP) y con el *Anemic Domain Model*.
4. Aplica la diferencia entre *Circuit Breaker* y Saga a un escenario de tu propio proyecto: ¿cuál de los dos patrones corresponde, y por qué?
5. ¿Qué diferencia de fondo hay entre el *fallback* de un *Circuit Breaker* y la compensación de una Saga, si los dos reaccionan a que "algo salió mal"?
6. Un mensaje de un diagrama de secuencia no aparece en ninguna ficha de clase de S8. ¿Qué inconsistencia revela eso, y qué documento hay que corregir primero?
7. ¿Por qué una capa anticorrupción es necesaria para un sistema externo real pero no para una integración entre dos módulos del mismo dominio?

## 4. Sustentación del catálogo UML

**Tabla 3. Distribución de tiempo de la sustentación**

| Momento | Tiempo | Quién | Propósito |
|---|---:|---|---|
| Alcance del proyecto (diapositiva) | 2-3 min | Grupal | **Una sola vez para los tres cursos** (si ADS, BD2 y LP2 se sustentan juntos) — qué hace el sistema, para quién, y su relación con el Proyecto Integrador. No se repite en BD2/LP2. |
| Arquitectura (diapositiva) | 2-3 min | Grupal | El diagrama de clases de dominio de referencia (entidades, agregación, composición, herencia) sobre el propio dominio del equipo, con al menos un patrón GoF/GRASP señalado directamente sobre el diagrama. |
| Asignación de tareas (diapositiva) | 2-3 min | Grupal | Quién modeló qué fase completa: dominio (S6), clases (S7), diseño por capas/transformación objeto-relacional (S8), diagramas dinámicos (S9), patrones (S10) o integración (S11) — cada integrante dueño de al menos una fase o un módulo. |
| Demo y sustentación | 3 min por integrante | Individual | Cada integrante presenta y sustenta la fase o el módulo que construyó, con base en la Tabla 1. |
| Preguntas | 3 min por integrante | Individual | Verificar dominio y aporte propio, con base en la Tabla 2 y las decisiones de `ads-producto.md`. |

**Tabla 4. Entregables obligatorios**

| Entregable | Evidencia mínima | Criterio de aceptación |
|---|---|---|
| Producto de unidad | [`ads-producto.md`](../../proyecto-integrador/u2/ads-producto.md) completo | Coherente con el sílabo y con el código real del proyecto |
| Sustentación individual | Preguntas y defensa por integrante (sección 3 y Tabla 2) | Autoría demostrada |

Secuencia sugerida de presentación (referencias a secciones de `ads-producto.md`):

1. Presentar el modelo de dominio y el diagrama de clases de referencia (secciones 1-2), explicando al menos un agregado y por qué su límite de consistencia es ese y no otro.
2. Mostrar el diseño de clases por capas y la transformación objeto-relacional de un módulo propio (sección 1, filas 3-4), con la matriz de trazabilidad dominio-clase-tabla-DTO.
3. Recorrer los diagramas dinámicos del escenario crítico (sección 1, fila 5), mostrando la matriz de consistencia contra las clases de S8 en verde.
4. Justificar al menos tres patrones GoF/GRASP realmente encontrados en el código propio (sección 1, fila 6), no solo nombrados.
5. Cerrar con el diseño de integración empresarial (sección 1, fila 7): la frontera síncrona protegida con *Circuit Breaker*, la frontera asíncrona con *Outbox*, y la Saga coreografiada con su compensación.
6. Mostrar la trazabilidad técnica U2 (sección 3) cruzando el diseño ADS contra la evidencia real de BD2 y LP2.

Criterios mínimos de aceptación:

- El modelo de dominio (S6) y el diagrama de clases (S7) son coherentes entre sí y con el código real del proyecto.
- El diseño de clases por capas y la transformación objeto-relacional (S8) tienen matriz de trazabilidad verificable contra BD2.
- Los diagramas dinámicos (S9) cubren el escenario crítico elegido, con su matriz de consistencia sin inconsistencias frente a S8.
- Cada patrón GoF/GRASP presentado (S10) se sustenta con un ejemplo real del código, no solo con la definición.
- El diseño de integración (S11) protege al menos un sistema externo real con capa anticorrupción y *Circuit Breaker*, y coordina al menos un proceso de varios pasos con una Saga coreografiada y su compensación, distinguida con claridad del *fallback*.
- Cada integrante responde individualmente al menos una pregunta de la Tabla 2 o de `ads-producto.md`.

## 5. Rúbrica de evaluación

La rúbrica (6 criterios: 5 cita literal de los criterios de evaluación del producto de la Unidad II + sustentación) vive en [`ads-producto.md`](../../proyecto-integrador/u2/ads-producto.md#4-rubrica-de-evaluacion), junto con la plantilla del producto. Úsala directamente desde ahí para calificar la sustentación de esta sesión — no se duplica aquí.
