# S06 - Evaluación de la Unidad I

## 1. Propósito de la evaluación

Esta sesión no enseña contenido nuevo: cierra la Unidad I de **LP2**. El sílabo (sesión 6) define dos actividades para esta evaluación:

1. Resolver la evaluación teórico-práctica de los temas de la Unidad I (sesiones 1 a 5).
2. Presentar y sustentar el Backend REST empresarial.

**Esta sesión coincide con el "Primer corte integrado" de los tres cursos**, según el cronograma del Proyecto Integrador. ADS ya evaluó su arquitectura en su propia sesión 5, la semana anterior; BD2 evalúa su motor transaccional Oracle esta misma semana, en su propia sesión 6. La sustentación de hoy es individual de LP2 — tu backend, tu código, tu evidencia —, pero la sección 4 exige además evidencia de que ese backend funciona sobre la arquitectura de ADS y el motor transaccional de BD2, no aislado.

## 2. Producto evaluado

Del sílabo, el producto de la Unidad I es:

> Backend REST empresarial con ORM, CRUD, objetos relacionados, operación cabecera–detalle, consultas, reportes, CORS, logs y pruebas.

El producto completo — plantilla-ejemplo con el contenido de BomERP — vive en [`lp2-demo.md`](../../proyecto-integrador/u1/lp2-demo.md): alcance arquitectónico, contrato REST, DTO principales, arquitectura backend, casos de prueba y trazabilidad con ADS y BD2. La estructura es exigible a todos (monolito modular verificado, un módulo transaccional con cabecera-detalle real, persistencia, consultas, CORS, logs y pruebas); el contenido de BomERP se reemplaza por el del propio proyecto de cada equipo.

### Lo que acumulaste sesión por sesión

Este producto no se construye en S06: se ensambla con lo que cada sesión anterior ya te pidió sobre tu propio proyecto.

**Tabla 1. De la sesión al backend evaluado**

| Sesión | Qué produjiste (tu propio proyecto) | Dónde queda en `lp2-demo.md` |
|---|---|---|
| S1 | Proyecto backend ejecutable, conectado a Oracle, con endpoint de verificación y un primer recurso REST. | 1. Alcance arquitectónico y 3. Contrato REST |
| S2 | CRUD REST completo de tu recurso principal, con DTO validado, mapeo explícito y manejo global de errores. | 3. Contrato REST |
| S3 | Objetos relacionados entre dos entidades, con DTO relacionado y navegación controlada. | 3. Contrato REST |
| S4 | Tu propia operación cabecera-detalle, con cálculos, una regla de negocio real y transacción atómica probada. | 3. Contrato REST y 4. DTO principales |
| S5 | Filtros combinados, ordenamiento, proyecciones, agregaciones y CORS configurado. | 3. Contrato REST |
| S6 (esta sesión) | Ensamblas todo lo anterior en un backend único y lo sustentas. | El backend completo + sección 4 de esta guía |

Lo que sustentas en S06 es **tu backend**: los recursos que tú construiste, con los datos y reglas de tu propio dominio — no el de BomERP. `lp2-demo.md` muestra cómo se ve ese backend terminado usando el ejemplo del docente; tu entregable real tiene la misma estructura, con el contenido que tú construiste en S1-S5.

## 3. Evaluación teórico-práctica (S1-S5)

Cubre los cinco temas dictados antes de esta sesión. El docente puede tomarla escrita, oral o mixta.

**Tabla 2. Temario de la evaluación teórico-práctica**

| Sesión | Tema | Qué puede evaluar el docente |
|---|---|---|
| S1 | Arquitectura backend REST profesional | Estructura del proyecto, configuración por ambientes, ORM, driver, conexión, contrato y versionado básico de API. |
| S2 | CRUD REST completo de una entidad principal | DTO de entrada/salida, mapeo explícito, validaciones, manejo global de errores y trazabilidad por petición. |
| S3 | Objetos relacionados mediante REST y ORM | Asociación entre entidades, DTO relacionado, navegación controlada y prevención de ciclos de serialización. |
| S4 | Operación de dominio con cabecera-detalle | DTO compuesto, cálculos, una regla de negocio real, transacción atómica y comunicación entre módulos. |
| S5 | Consultas empresariales y CORS | Filtros combinados, ordenamiento, proyecciones, agregaciones y por qué CORS es una restricción del navegador, no del servidor. |

Preguntas de referencia (el docente puede formular equivalentes):

1. ¿Por qué tu backend es un único proyecto Maven, y qué verificación automática impide que un módulo acceda al repositorio de otro?
2. En tu operación cabecera-detalle, ¿qué línea de código hace posible que un fallo a mitad de la operación revierta todo, no solo la última línea?
3. ¿Por qué tu DTO de entrada es una clase distinta del de salida, en vez de reutilizar uno solo?
4. Si tu filtro combina tres criterios opcionales, ¿por qué una sola consulta con `(:param IS NULL OR ...)` es mejor que encadenar métodos derivados?
5. ¿Qué evidencia concreta demuestra que CORS está configurado por propiedad y no fijo en el código?

## 4. Sustentación del backend

**Tabla 3. Distribución de tiempo por integrante**

| Momento | Tiempo | Propósito |
|---|---:|---|
| Presentación técnica | 8 min | Explicar el backend (sección 2), las decisiones tomadas y su justificación. |
| Demo técnica | 5 min | Ejecutar el CRUD, la operación cabecera-detalle y las consultas en vivo, incluido un caso de error. |
| Preguntas individuales | 5 min | Verificar dominio y aporte propio, con base en la Tabla 2. |

**Tabla 4. Entregables obligatorios**

| Entregable | Evidencia mínima | Criterio de aceptación |
|---|---|---|
| Producto de unidad | [`lp2-demo.md`](../../proyecto-integrador/u1/lp2-demo.md) completo | Coherente con el sílabo y con el código real ejecutable |
| Evidencia de integración | Backend conectado a los esquemas Oracle de BD2, endpoints vivos, verificación automática de módulos en verde | Trazabilidad verificable con ADS y BD2, no solo documentada |
| Sustentación individual | Preguntas y defensa por integrante (sección 3) | Autoría demostrada |

Secuencia sugerida de presentación (referencias a secciones de `lp2-demo.md`):

1. Presentar el alcance arquitectónico y el contrato REST.
2. Ejecutar el CRUD completo en vivo: un caso de éxito y un caso inválido (`400`).
3. Ejecutar la operación cabecera-detalle: un caso de éxito y un caso de rollback provocado a propósito.
4. Ejecutar una consulta con filtros combinados y el reporte agregado (`/resumen`).
5. Mostrar la verificación automática de módulos en verde y los esquemas Oracle reales de BD2 conectados.
6. Cerrar con la tabla de trazabilidad con ADS y BD2 de `lp2-demo.md`, explicando al menos una fila con evidencia en vivo.

Criterios mínimos de aceptación:

- El backend arranca y conecta con Oracle sin errores, contra el esquema real que BD2 construyó.
- El CRUD y la operación cabecera-detalle funcionan con al menos un caso de éxito y uno de error cada uno.
- Al menos un filtro combinado y el reporte agregado responden con datos reales.
- La verificación automática de módulos está en verde y se muestra en vivo, no solo se menciona.
- Cada integrante responde individualmente al menos una pregunta de la Tabla 2.

## 5. Rúbrica de evaluación

La rúbrica (6 criterios: 5 cita literal del resultado de aprendizaje de la Unidad I + sustentación) vive en [`lp2-demo.md`](../../proyecto-integrador/u1/lp2-demo.md#8-rubrica-de-evaluacion), junto con la plantilla del producto y su trazabilidad con la malla curricular (CE023 Nivel 2). Úsala directamente desde ahí para calificar la sustentación de esta sesión — no se duplica aquí.
