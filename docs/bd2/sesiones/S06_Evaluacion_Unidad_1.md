# S06 - Evaluación de la Unidad I

## 1. Propósito de la evaluación

Esta sesión no enseña contenido nuevo: cierra la Unidad I de **BD2**. El sílabo (sesión 6) define dos actividades para esta evaluación:

1. Resolver la evaluación teórico-práctica de los temas de la Unidad I (sesiones 1 a 5).
2. Presentar y sustentar el Motor transaccional Oracle optimizado.

**Esta sesión coincide con el "Primer corte integrado" de los tres cursos**, según el cronograma del Proyecto Integrador. ADS ya evaluó su arquitectura en su propia sesión 5, la semana anterior; LP2 evalúa su backend REST esta misma semana, en su propia sesión 6. La sustentación de hoy es individual de BD2 — tu esquema, tus objetos Oracle, tu evidencia —, pero la sección 4 exige además evidencia de que ese motor transaccional sostiene realmente al backend de LP2, no aislado.

## 2. Producto evaluado

Del sílabo, el producto de la Unidad I es:

> Motor transaccional implementado en Oracle XE con PL/SQL, triggers, excepciones, optimización de consultas e índices documentados y sustentados.

El producto completo — plantilla-ejemplo con el contenido de BomERP — vive en [`bd2-producto.md`](../../proyecto-integrador/u1/bd2-producto.md): scripts, objetos Oracle, reglas de negocio y transaccionales, manejo de excepciones, selectividad e índices, y evidencia de integración con ADS y LP2. La estructura es exigible a todos (motor transaccional con reglas de negocio en PL/SQL, manejo de excepciones, auditoría y optimización con índices según selectividad medida); el contenido de BomERP se reemplaza por el del propio proyecto de cada equipo.

### Lo que acumulaste sesión por sesión

Este producto no se construye en S06: se ensambla con lo que cada sesión anterior ya te pidió sobre tu propio proyecto.

**Tabla 1. De la sesión al motor transaccional evaluado**

| Sesión | Qué produjiste (tu propio proyecto) | Dónde queda en `bd2-producto.md` |
|---|---|---|
| S1 | Esquema y tablas base, con procedimientos y funciones PL/SQL para tus operaciones. | 2. Objetos Oracle U1 |
| S2 | Triggers para automatizar una regla de negocio y registrar auditoría básica. | 2. Objetos Oracle U1 y 3. Reglas de negocio |
| S3 | Manejo de excepciones predefinidas y personalizadas, con registro de errores. | 4. Manejo de excepciones |
| S4 | Una consulta representativa optimizada con `EXPLAIN PLAN` y `DBMS_STATS`. | 2. Objetos Oracle U1 |
| S5 | Selectividad medida e índices B-Tree, Bitmap y Function-Based creados según esa medición. | 5. Selectividad e índices |
| S6 (esta sesión) | Ensamblas todo lo anterior en un motor transaccional único y lo sustentas. | El motor completo + sección 4 de esta guía |

Lo que sustentas en S06 es **tu motor transaccional**: los objetos que tú construiste, con las reglas y los datos de tu propio dominio — no el de BomERP. `bd2-producto.md` muestra cómo se ve ese motor terminado usando el ejemplo del docente; tu entregable real tiene la misma estructura, con el contenido que tú construiste en S1-S5.

## 3. Evaluación teórico-práctica (S1-S5)

Cubre los cinco temas dictados antes de esta sesión. El docente puede tomarla escrita, oral o mixta.

**Tabla 2. Temario de la evaluación teórico-práctica**

| Sesión | Tema | Qué puede evaluar el docente |
|---|---|---|
| S1 | PL/SQL aplicado al negocio | Creación del esquema y las tablas base, procedimientos, funciones y parámetros `IN`/`OUT`/`IN OUT`. |
| S2 | Triggers DML | Pseudo-registros `:OLD`/`:NEW`, reglas automáticas de negocio y auditoría básica. |
| S3 | Manejo de excepciones y robustez | Excepciones predefinidas frente a personalizadas, registro de errores y tolerancia a fallos. |
| S4 | Optimización de consultas SQL | Cost Based Optimizer, `EXPLAIN PLAN`, `DBMS_STATS` y buenas prácticas SQL sin tocar índices. |
| S5 | Índices para optimización | B-Tree, Bitmap, Function-Based Index y por qué la selectividad decide, no la intuición. |

Preguntas de referencia (el docente puede formular equivalentes):

1. ¿Por qué un trigger `BEFORE` puede rechazar una operación y uno `AFTER` no?
2. ¿Qué diferencia hay entre una excepción predefinida de Oracle y una personalizada con `RAISE_APPLICATION_ERROR`, y cuándo usarías cada una?
3. ¿Por qué `EXPLAIN PLAN` no ejecuta realmente la consulta, y qué columna interpretarías mal si pensaras que `COST` es una medida absoluta?
4. ¿Qué mide la selectividad de una columna, y por qué no basta con eso para decidir si el optimizador usará un índice en una consulta concreta?
5. En tu propio proyecto, ¿qué columna tiene la selectividad más baja, y por qué decidiste (o no) crearle un índice?

## 4. Sustentación del motor transaccional

**Tabla 3. Distribución de tiempo por integrante**

| Momento | Tiempo | Propósito |
|---|---:|---|
| Presentación técnica | 8 min | Explicar el motor transaccional (sección 2), las decisiones tomadas y su justificación. |
| Demo técnica | 5 min | Ejecutar procedimientos, disparar un trigger, provocar una excepción y comparar un `EXPLAIN PLAN` antes/después de un índice. |
| Preguntas individuales | 5 min | Verificar dominio y aporte propio, con base en la Tabla 2. |

**Tabla 4. Entregables obligatorios**

| Entregable | Evidencia mínima | Criterio de aceptación |
|---|---|---|
| Producto de unidad | [`bd2-producto.md`](../../proyecto-integrador/u1/bd2-producto.md) completo | Coherente con el sílabo y con los objetos Oracle reales |
| Evidencia de integración | Esquemas conectados y consumidos por el backend real de LP2, `EXPLAIN PLAN`/selectividad documentados | Trazabilidad verificable con ADS y LP2, no solo documentada |
| Sustentación individual | Preguntas y defensa por integrante (sección 3) | Autoría demostrada |

Secuencia sugerida de presentación (referencias a secciones de `bd2-producto.md`):

1. Presentar los objetos Oracle y las reglas de negocio implementadas.
2. Disparar en vivo un trigger de regla de negocio y uno de auditoría, mostrando el registro generado.
3. Provocar una excepción personalizada y mostrar su registro en la tabla de errores.
4. Comparar un `EXPLAIN PLAN` antes y después de un índice, explicando la selectividad medida que justificó la decisión.
5. Mostrar el caso de un índice descartado (tabla de selectividad e índices, fila `ESTADO`) y explicar por qué no se creó.
6. Cerrar con la tabla de evidencia de integración con ADS y LP2 de `bd2-producto.md`, explicando al menos una fila con evidencia en vivo desde el backend de LP2.

Criterios mínimos de aceptación:

- Los objetos Oracle existen y responden en vivo, no solo en captura de pantalla.
- Al menos un trigger de regla de negocio y uno de auditoría se disparan y se explican correctamente.
- Al menos una excepción personalizada se prueba con un caso real.
- La comparación de `EXPLAIN PLAN` incluye la selectividad medida que justificó el índice creado (o descartado).
- Cada integrante responde individualmente al menos una pregunta de la Tabla 2.

## 5. Rúbrica de evaluación

La rúbrica (6 criterios: 5 cita literal del resultado de aprendizaje de la Unidad I + sustentación) vive en [`bd2-producto.md`](../../proyecto-integrador/u1/bd2-producto.md#7-rubrica-de-evaluacion), junto con la plantilla del producto y su trazabilidad con la malla curricular (CE023 Nivel 1). Úsala directamente desde ahí para calificar la sustentación de esta sesión — no se duplica aquí.
