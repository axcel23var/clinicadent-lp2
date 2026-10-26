# BD2 - Producto de Unidad 1

**Esta es la plantilla-ejemplo del producto de Unidad 1 de BD2.** La estructura (objetos Oracle, reglas de negocio y transaccionales, manejo de excepciones, selectividad e índices, evidencia de integración) es exigible a todos: motor transaccional con reglas de negocio en PL/SQL, manejo de excepciones, auditoría y optimización con índices según selectividad medida. El contenido de BomERP (esquemas, tablas, paquetes PL/SQL y triggers concretos) es el ejemplo real que muestra cómo se ve terminada — cada sede (Lima, Juliaca, Tarapoto) y cada grupo reemplaza ese contenido por el de su propio proyecto, definido en su propio [brief.md](../brief.md) de S2, sin cambiar la estructura.

## Producto

**Motor transaccional Oracle optimizado.**

Este producto implementa lógica de negocio en Oracle mediante PL/SQL, triggers, excepciones, auditoría básica, consultas optimizadas e índices. La base no se trabaja como ejercicio aislado: soporta los endpoints y reglas del backend LP2.

## 1. Scripts del producto

Los scripts se agregan **por sesión de BD2**, a medida que cada una les da contenido real — no se pre-crean tablas/objetos de sesiones que todavía no se dictaron (mismo criterio que la arquitectura de módulos de LP2, ver [ADR-002](../../lp2/adr/ADR-002-spring-modulith.md)).

**Tabla 1. Scripts del producto**

| Script | Sesión BD2 | Uso |
|---|---|---|
| [S01_01_esquemas.sql](oracle/S01_01_esquemas.sql) | [S1](../../bd2/sesiones/S01_PLSQL_Aplicado_Negocio.md) | Usuarios `BOM_CATALOGO` (propietario) y `BOMERP_APP` (técnico de LP2). |
| [S01_02_tablas.sql](oracle/S01_02_tablas.sql) | [S1](../../bd2/sesiones/S01_PLSQL_Aplicado_Negocio.md) | Tablas `CATEGORIAS`/`PRODUCTOS` y permisos de `BOMERP_APP` sobre ellas. |
| [S01_03_plsql.sql](oracle/S01_03_plsql.sql) | [S1](../../bd2/sesiones/S01_PLSQL_Aplicado_Negocio.md) | Función y procedimientos PL/SQL del catálogo. |
| [S02_triggers_dml_auditoria.sql](oracle/S02_triggers_dml_auditoria.sql) | [S2](../../bd2/sesiones/S02_Triggers_DML_Auditoria.md) | Trigger de regla de negocio (`TRG_PRODUCTO_PRECIO_BU`) y trigger + tabla de auditoría de precio/stock (`TRG_PRODUCTO_AUDITORIA`, `PRODUCTO_AUDITORIA`). |
| (excepciones agregadas directo sobre los procedimientos de S1) | [S3](../../bd2/sesiones/S03_Excepciones_Robustez.md) | Manejo de excepciones personalizadas y tabla de registro de errores (`LOG_ERRORES`). |
| [S04_01_esquemas.sql](oracle/S04_01_esquemas.sql) | [S4](../../bd2/sesiones/S04_Optimizacion_Consultas_SQL.md) | Usuario `BOM_VENTAS`. |
| [S04_02_tablas.sql](oracle/S04_02_tablas.sql) | [S4](../../bd2/sesiones/S04_Optimizacion_Consultas_SQL.md) | Tablas `VENTAS`/`DETALLE_VENTAS`. |
| [S04_optimizacion_consultas.sql](oracle/S04_optimizacion_consultas.sql) | [S4](../../bd2/sesiones/S04_Optimizacion_Consultas_SQL.md) | Volumen de prueba, `EXPLAIN PLAN`, `DBMS_STATS` y reescritura de la consulta representativa. |
| [S05_indices_optimizacion.sql](oracle/S05_indices_optimizacion.sql) | [S5](../../bd2/sesiones/S05_Indices_Optimizacion.md) | Selectividad medida e índices B-Tree, Bitmap y Function-Based. |

Pendiente (se agrega cuando esa sesión de BD2 se documente): esquema `BOM_SEGURIDAD` (S10 de LP2).

## 2. Objetos Oracle U1

**Estado al cierre de la Unidad 1 (S6):**

**Tabla 2. Objetos Oracle U1**

| Objeto | Propósito | Relación con LP2 |
|---|---|---|
| `BOM_CATALOGO.CATEGORIAS`/`PRODUCTOS` | Catálogo heredado de Ciclo 3. | Recursos `/api/v1/categorias` y `/api/v1/productos`. |
| `BOM_CATALOGO.LOG_ERRORES` | Registro de errores capturados por los procedimientos/función del catálogo (S3). | Ninguno directo — el backend no consulta esta tabla. |
| `BOM_CATALOGO.PRODUCTO_AUDITORIA` + `TRG_PRODUCTO_AUDITORIA` | Auditoría de cambios de precio/stock. | `POST`/`PUT` sobre `/api/v1/productos` (cualquier alta o cambio la dispara). |
| `BOM_VENTAS.VENTAS`/`DETALLE_VENTAS` | Operación transaccional principal. | Recurso `/api/v1/ventas`. |
| `IX_VENTAS_FECHA` (B-Tree) | Consultas filtradas por rango de fecha. | `GET /api/v1/ventas?desde=&hasta=`. |
| `IX_LOG_ERRORES_OBJETO` (Bitmap) | Diagnóstico de errores agrupados por objeto que falló. | Ninguno directo. |
| `IX_VENTAS_FECHA_DIA` (Function-Based, sobre `TRUNC(FECHA)`) | Reporte de ventas por día calendario. | `GET /api/v1/ventas/resumen`. |

## 3. Reglas de negocio y transaccionales

**Tabla 3. Reglas de negocio y transaccionales**

| Regla | Implementación Oracle |
|---|---|
| Un producto se registra con una categoría que debe existir. | `SP_REGISTRAR_PRODUCTO`, captura `ORA-02291` y la registra en `LOG_ERRORES`. |
| Un descuento no puede dejar el precio fuera de rango razonable. | `TRG_PRODUCTO_PRECIO_BU` (`BEFORE UPDATE`), rechaza con `RAISE_APPLICATION_ERROR` antes de escribir. |
| Todo cambio de precio o stock queda auditado, sin que el backend lo sepa. | `TRG_PRODUCTO_AUDITORIA` (`AFTER INSERT`/`UPDATE`/`DELETE`), disparado por Oracle mismo. |
| Una venta no puede registrarse con stock insuficiente. | Validado desde el servicio de `ventas` en LP2 (S4), consultando `catalogo` vía su servicio público — regla de estado, no de forma, por eso vive en el service y no en un `CHECK`. |
| Un producto inexistente al consultar su precio responde con causa clara. | `FN_OBTENER_PRECIO_PRODUCTO`, captura `NO_DATA_FOUND` y la registra en `LOG_ERRORES`. |

## 4. Manejo de excepciones

**Tabla 4. Manejo de excepciones**

| Situación | Excepción esperada |
|---|---|
| Categoría inexistente al registrar un producto. | `ORA-02291` capturado, `RAISE_APPLICATION_ERROR(-20010, ...)`. |
| Porcentaje de descuento fuera de rango (0-100). | `RAISE_APPLICATION_ERROR(-20011, ...)`, sin código Oracle previo — la regla la crea el propio procedimiento. |
| Producto inexistente al consultar su precio. | `NO_DATA_FOUND` (código `100`), `RAISE_APPLICATION_ERROR(-20012, ...)`. |

## 5. Selectividad e índices (S5)

Ningún índice se crea sin medir selectividad primero (`COUNT(DISTINCT columna) / COUNT(*)`):

**Tabla 5. Selectividad e índices**

| Columna candidata | Selectividad | Índice creado |
|---|---|---|
| `VENTAS.FECHA` | Alta (cercana a 1) | B-Tree (`IX_VENTAS_FECHA`) |
| `LOG_ERRORES.OBJETO` | Baja (pocos valores distintos) | Bitmap (`IX_LOG_ERRORES_OBJETO`) |
| `TRUNC(VENTAS.FECHA)` | La de la expresión, no la de la columna | Function-Based (`IX_VENTAS_FECHA_DIA`) |
| `VENTAS.ESTADO` | Muy baja (un único valor, `EstadoVenta.REGISTRADA` en LP2 S4) | **Ninguno** — se creó, se confirmó que el optimizador no lo usaba, y se eliminó. |

## 6. Evidencia de integración

**Tabla 6. Evidencia de integración con ADS y LP2**

| BD2 | ADS | LP2 |
|---|---|---|
| `TRG_PRODUCTO_AUDITORIA` | Atributo de auditabilidad | `POST`/`PUT` sobre `/api/v1/productos`. |
| `IX_VENTAS_FECHA` | Atributo de rendimiento | `GET /api/v1/ventas?desde=&hasta=`. |
| `LOG_ERRORES` + excepciones personalizadas | Robustez del motor | `GlobalExceptionHandler` (manejo global de errores). |
| `EXPLAIN PLAN`/`DBMS_STATS` sobre la consulta representativa (S4) | — | Consulta de reporte agregado (`GET /api/v1/ventas/resumen`, LP2 S5). |

Las FK entre esquemas conservan la integridad porque todos los objetos pertenecen a una sola base Oracle. `BOMERP_APP` ejecuta la aplicación, pero no es propietario de tablas ni paquetes.

## 7. Rúbrica de Evaluación

**Tabla 7. Rúbrica de evaluación de la Unidad 1**

| Criterio | Peso | CE / Nivel | A (20 pts) | B (15 pts) | C (10 pts) | D (5 pts) | Calificación obtenida |
|---|---:|---|---|---|---|---|---:|
| 1. Implementa procedimientos y funciones PL/SQL alineados al negocio | 16% | CE023-N1 | Procedimientos y funciones correctos, probados con casos válidos e inválidos, alineados a una regla real del proyecto. | Procedimientos y funciones correctos, con pruebas parciales. | Procedimientos incompletos o sin alineación clara al negocio. | No presenta procedimientos ni funciones PL/SQL. | |
| 2. Automatiza reglas mediante triggers DML | 16% | CE023-N1 | Al menos un trigger de regla de negocio y uno de auditoría, disparados y verificados en vivo. | Triggers presentes, con verificación parcial. | Un solo trigger funcional, o sin verificación clara. | No presenta triggers. | |
| 3. Controla errores mediante manejo de excepciones | 16% | CE023-N1 | Excepciones predefinidas y personalizadas, con registro de errores probado con casos reales. | Manejo de excepciones presente, con registro parcial. | Manejo de excepciones incompleto o sin registro. | No maneja excepciones. | |
| 4. Analiza y mejora consultas mediante Explain Plan, CBO y DBMS_STATS | 16% | CE023-N1 | Comparación completa (antes/después de estadísticas y de una reescritura), con `COST`/`ROWS` interpretados correctamente. | Comparación presente, con interpretación parcial. | Un solo `EXPLAIN PLAN` capturado, sin comparación real. | No presenta `EXPLAIN PLAN`. | |
| 5. Aplica estrategias de indexación según selectividad y necesidades de consulta | 16% | CE023-N1 | Selectividad medida antes de crear cada índice, con al menos un caso de índice correctamente descartado. | Índices creados con selectividad medida, sin caso de descarte. | Índices creados sin medir selectividad. | No presenta índices ni selectividad medida. | |
| 6. Sustentación | 20% | CG | Sustenta con claridad y profesionalismo su aporte individual, respondiendo con precisión las preguntas del jurado. | Sustenta con solvencia, con detalles menores en claridad, orden o precisión. | Sustenta con dificultad; claridad, orden o precisión insuficientes. | No sustenta adecuadamente ni demuestra su aporte individual. | |

Nota final = suma de (`Peso` × `Puntos de la calificación obtenida`) / 100 × 20.

`CE023-N1` = Nivel 1 de CE023 (Programación) — la rama que se satisface programando el motor transaccional del lado del servidor. `CG` = Competencia General del sílabo de BD2 — con una inconsistencia del propio documento fuente que hay que resolver antes de usar esta etiqueta en un informe formal: la sección III del sílabo la nombra "PENSAMIENTO SUPERIOR" (Firmeza de propósito, ejecución, dominio propio, mantener esfuerzo), pero la tabla de evaluación de la sección VIII la nombra "Servicio y misión" — no son el mismo texto y no está claro cuál es la vigente. No es CE023: los criterios 1-5 ya son la evidencia técnica, incluida su verificación en vivo; el criterio 6 verifica aporte individual y comunicación.

**Tabla 8. Subaspectos de la sustentación (Unidad 1)**

El criterio 6 se evalúa con los mismos 6 subaspectos de la sustentación integral del Proyecto Integrador ([Guía de Sustentación Final](../u3/guia-sustentacion.md#subaspectos-de-la-sustentacion-integral)) — exigibles desde esta primera sustentación de unidad, no solo en la sustentación final del ciclo (Unidad 3).

| Subaspecto | Qué observa en Unidad 1 |
|---|---|
| 1. Aporte individual | Cada integrante demuestra lo que construyó de su propio motor transaccional. |
| 2. Comunicación y orden | Claridad, estructura, tiempo y lenguaje técnico durante la presentación. |
| 3. Presentación personal y actitud | Puntualidad, vestimenta limpia y adecuada, higiene, cabello ordenado, actitud profesional, respeto, honestidad y coherencia con los valores y principios cristianos de la institución. |
| 4. Repositorio y estándares | Topics académicos configurados desde S2, organización, commits y reproducibilidad de los scripts Oracle. |
| 5. MkDocs o equivalente | Documentación de Unidad 1 publicada, navegable y alineada con `bd2-producto.md`. |
| 6. Pitch/demo ejecutiva | Introducción breve del motor transaccional y su avance, con apoyo visual (.pptx, Canva o equivalente) — no reemplaza la demo técnica de S06, la precede. |

Para usar la rúbrica con IA, solicita:

```text
Evalúa la sustentación y el producto (bd2-producto.md o la sección 2 de la guía S06) usando la rúbrica de esta sección.
Para cada criterio selecciona la calificación obtenida: A=20, B=15, C=10, D=5.
Justifica brevemente cada nivel con evidencia concreta (objetos Oracle, planes de ejecución, selectividad).
Calcula la nota final con la fórmula: suma de (Peso × Puntos de la calificación obtenida) / 100 × 20.
Indica 2 fortalezas y 2 recomendaciones para lo que sigue en Unidad II.
```

## 8. Trazabilidad y procedencia de la rúbrica

Los primeros cinco criterios son cita literal del resultado de aprendizaje de la Unidad I en el sílabo de BD2; el sexto (Sustentación) corresponde a la sustentación exigida por el mismo sílabo (sesión 6, actividad 2).

**Con la malla curricular:** los criterios 1-5 corresponden al **Nivel 1 de CE023** (Programación) — la rama de esa competencia que se satisface programando el motor transaccional del lado del servidor (Unidad 1 de BD2), distinta de la rama que otros cursos (`FP`, `POO`, `LP1`) satisfacen construyendo una plataforma completa de otro tipo. La administración de la instancia Oracle a escala empresarial (**Nivel 2 de CE022**, Ingeniería de la Información) no se evalúa aquí: se completa en las Unidades 2 y 3 de BD2. El criterio 6 (Sustentación) es transversal y no forma parte de la definición de la competencia.
