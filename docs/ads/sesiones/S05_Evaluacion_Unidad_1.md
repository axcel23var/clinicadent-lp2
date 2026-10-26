# S05 - Evaluación de la Unidad I

## 1. Propósito de la evaluación

Esta sesión no enseña contenido nuevo: cierra la Unidad I de **ADS**. El sílabo (sesión 5) define dos actividades:

1. Resolver la evaluación teórico-práctica de los temas de la Unidad I (sesiones 1 a 4).
2. Presentar y sustentar la Arquitectura documentada mediante vistas arquitectónicas y principios de diseño aplicados.

Esta sesión evalúa únicamente el producto de ADS. La integración con el código real de BD2 y LP2 se verifica en la sesión 6 ("Primer corte integrado"), no aquí.

## 2. Producto evaluado

Del sílabo, el producto de la Unidad I es:

> Arquitectura documentada con vistas de contexto, contenedores, componentes, principios de diseño aplicados y justificación del estilo arquitectónico elegido.

El producto completo — plantilla-ejemplo con el contenido de BomERP — vive en [`ads-producto.md`](../../proyecto-integrador/u1/ads-producto.md): contexto técnico, atributos de calidad, vistas C1-C3, principios de diseño, ADR y trazabilidad técnica U1. La estructura es exigible a todos; el contenido de BomERP se reemplaza por el del propio proyecto de cada equipo.

### Lo que acumulaste sesión por sesión

Este producto no se construye en S05: se ensambla con lo que cada sesión anterior ya te pidió sobre tu propio proyecto.

**Tabla 1. De la sesión al documento final**

| Sesión | Qué produjiste (tu propio proyecto) | Dónde queda en `ads-producto.md` |
|---|---|---|
| S1 | Mapa arquitectónico inicial: contexto, stakeholders, atributos de calidad priorizados y primeras decisiones técnicas. | 1. Contexto técnico y 2. Atributos de calidad |
| S2 | Vistas C1 (contexto), C2 (contenedores) y una primera versión de C3/C4 de tu propio sistema. | 3-4. Vistas C1, C2 |
| S3 | Evaluación de tus propios módulos contra SOLID, cohesión, acoplamiento, modularidad y abstracción, con al menos un hallazgo real. | 6. Principios de diseño aplicados |
| S4 | Comparación de estilos arquitectónicos contra tu propio proyecto, con trade-offs y la justificación formal del estilo elegido. | 5. Vista C3 (final) y 7. ADR iniciales |
| S5 (esta sesión) | Ensamblas todo lo anterior en un solo documento y lo sustentas. | El documento completo + sección 4 de esta guía |

Lo que sustentas en S05 es **tu arquitectura**: el estilo que tú elegiste en S4, justificado frente a alternativas, documentado con tus propias vistas C1-C3 y tus propios principios aplicados — no la de BomERP.

## 3. Evaluación teórico-práctica (S1-S4)

Cubre los cuatro temas dictados antes de esta sesión. El docente puede tomarla escrita, oral o mixta.

**Tabla 2. Temario de la evaluación teórico-práctica**

| Sesión | Tema | Qué puede evaluar el docente |
|---|---|---|
| S1 | Fundamentos de arquitectura de software | Rol de la arquitectura, stakeholders, atributos de calidad y su relación con los requerimientos. |
| S2 | Modelo C4 y vistas arquitectónicas | Diferencia entre C1, C2, C3 y C4; qué información pertenece a cada nivel y por qué la vista C3 del propio proyecto no expone el detalle hexagonal/capas de su módulo transaccional. |
| S3 | Diseño estructural y principios SOLID | Aplicación de SOLID, cohesión, acoplamiento, modularidad y abstracción sobre los módulos reales del propio proyecto (equivalentes a `catalogo`/`ventas` en el ejemplo BomERP). |
| S4 | Arquitecturas modernas | Monolito modular vs. microservicios vs. hexagonal vs. Clean Architecture, cuándo DDD orienta hacia hexagonal/Clean, los errores comunes de cada estilo, y qué es un ADR y por qué documentar una decisión arquitectónica. |

Preguntas de referencia (el docente puede formular equivalentes):

1. ¿Por qué tu proyecto eligió su estilo arquitectónico (monolito modular, hexagonal u otro) y no alguna de las demás alternativas comparadas en S4?
2. Un módulo pone `@Entity` en su clase de dominio y la llama "hexagonal". ¿Por qué eso no es hexagonal?
3. Un método solo hace `repository.save(entidad)` sin ninguna regla de negocio. ¿Por qué llamarlo "caso de uso" es incorrecto?
4. Aplica la prueba del papel y lápiz a una regla de negocio de tu propio módulo transaccional: ¿es una entidad o un caso de uso?
5. ¿Qué gana un proyecto al verificar automáticamente los límites entre módulos, en vez de solo documentarlos como convención?
6. ¿Qué cambiaría en tu vista C3 si tu módulo transaccional migrara a hexagonal? ¿Ese cambio afectaría tu vista C2?
7. ¿Por qué un ADR debe registrar también las alternativas descartadas y no solo la decisión final?

## 4. Sustentación de la arquitectura

**Tabla 3. Distribución de tiempo por integrante**

| Momento | Tiempo | Propósito |
|---|---:|---|
| Presentación técnica | 10 min | Explicar el producto (`ads-producto.md`), las decisiones tomadas y su justificación. |
| Preguntas individuales | 8 min | Verificar dominio y aporte propio, con base en la Tabla 2 y las decisiones de `ads-producto.md`. |

**Tabla 4. Entregables obligatorios**

| Entregable | Evidencia mínima | Criterio de aceptación |
|---|---|---|
| Producto de unidad | [`ads-producto.md`](../../proyecto-integrador/u1/ads-producto.md) completo | Coherente con el sílabo y con el código real del proyecto |
| Sustentación individual | Preguntas y defensa por integrante (sección 3 y Tabla 2) | Autoría demostrada |

Secuencia sugerida de presentación (referencias a secciones de `ads-producto.md`):

1. Presentar el dominio y el problema técnico (sección 1, Contexto técnico).
2. Recorrer las vistas C1, C2 y C3 (secciones 3-5) explicando qué decisión arquitectónica sostiene cada una.
3. Justificar el estilo elegido (monolito modular con capas internas) frente a las alternativas de S4, citando al menos un error común que se evitó.
4. Mostrar los principios SOLID aplicados (sección 6, Principios de diseño) sobre código real de `catalogo` o `ventas`, con verificación automática de límites en verde.
5. Cerrar con la trazabilidad técnica (sección 8) y los ADR previstos aún no formalizados (sección 7).

Criterios mínimos de aceptación:

- Las tres vistas (C1, C2, C3) son coherentes entre sí y con el código real del proyecto.
- El estilo arquitectónico elegido está justificado frente a al menos una alternativa de S4.
- Cada principio SOLID (sección 6 de `ads-producto.md`) se sustenta con un ejemplo real, no solo con la definición, con verificación automática de límites en verde.
- Cada integrante responde individualmente al menos una pregunta de la Tabla 2 o de `ads-producto.md`.

## 5. Rúbrica de evaluación

La rúbrica (6 criterios: 5 cita literal del resultado de aprendizaje de la Unidad I + sustentación) vive en [`ads-producto.md`](../../proyecto-integrador/u1/ads-producto.md#9-rubrica-de-evaluacion), junto con la plantilla del producto. Úsala directamente desde ahí para calificar la sustentación de esta sesión — no se duplica aquí.
