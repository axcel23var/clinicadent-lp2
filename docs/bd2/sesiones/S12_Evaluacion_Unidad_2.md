# S12 - Evaluación de la Unidad II

## 1. Propósito de la evaluación

Esta sesión no enseña contenido nuevo: cierra la Unidad II de **BD2**. El sílabo (sesión 12) define dos actividades:

1. Resolver la evaluación teórico-práctica de los temas de la Unidad II (sesiones 7 a 11).
2. Presentar y sustentar la Base de datos empresarial administrada, optimizada y asegurada.

**Esta sesión coincide con el "Segundo corte integrado" de los tres cursos**, según el cronograma del Proyecto Integrador: ADS evalúa su catálogo UML esta misma semana, en su propia sesión 12; LP2 evalúa su SPA segura esta misma semana, en su propia sesión 12. La sustentación de hoy es individual de BD2 — tus objetos Oracle, tu evidencia de administración —, pero la sección 4 exige además evidencia de que esa base administrada sigue sosteniendo realmente al backend de LP2, no aislada.

## 2. Producto evaluado

Del sílabo, el producto de la Unidad II es:

> Base de datos empresarial que continúa el producto U1 e incorpora administración del motor Oracle, almacenamiento, seguridad, auditoría, optimización y particionamiento.

El producto completo — plantilla-ejemplo con el contenido de BomERP — vive en [`bd2-producto.md`](../../proyecto-integrador/u2/bd2-producto.md): scripts por sesión, evidencias esperadas, evidencia de integración con ADS y LP2, rúbrica de evaluación y trazabilidad de esa rúbrica contra la malla curricular. La estructura es exigible a todos; el contenido de BomERP se reemplaza por el del propio proyecto de cada equipo.

### Lo que acumulaste sesión por sesión

Este producto no se construye en S12: se ensambla con lo que cada sesión anterior ya te pidió sobre tu propio proyecto.

**Tabla 1. De la sesión a la base de datos administrada**

| Sesión | Qué produjiste (tu propio proyecto) | Dónde queda en `bd2-producto.md` |
|---|---|---|
| S7 | Arquitectura de tu propia instancia: componentes de SGA/PGA medidos, procesos *background* identificados, y una conexión `SYSDBA` distinguida de una de aplicación. | 2. Evidencias esperadas (Arquitectura e instancia) |
| S8 | Modelo de seguridad propio: roles por función, usuario de aplicación recortado a mínimo privilegio, usuario de solo lectura, y matriz de accesos verificada contra el diccionario de datos. | 1. Scripts (S8) y 2. Evidencias esperadas (Seguridad) |
| S9 | Estructura de almacenamiento propia (tablespace, datafile, segmentos) y modelo de auditoría con Unified Auditing, con evidencia real capturada. | 1. Scripts (S9) y 2. Evidencias esperadas (Almacenamiento, Auditoría) |
| S10 | Consulta crítica de tu propio proyecto reoptimizada con estadísticas actuales, un intento real de AWR (o su alternativa con vistas dinámicas), y un índice compuesto decidido con selectividad medida. | 1. Scripts (S10) y 2. Evidencias esperadas (Rendimiento) |
| S11 | Tabla de gran volumen de tu propio proyecto particionada, con *partition pruning* verificado y también roto a propósito, variantes de particionamiento documentadas, y la decisión medida `DROP PARTITION` frente a `DELETE`. | 1. Scripts (S11) y 2. Evidencias esperadas (Escalabilidad) |
| S12 (esta sesión) | Ensamblas todo lo anterior en una base de datos administrada única y lo sustentas. | El producto completo + sección 4 de esta guía |

Lo que sustentas en S12 es **tu base de datos administrada**: los objetos que tú construiste, con las reglas y los datos de tu propio dominio — no el de BomERP. `bd2-producto.md` muestra cómo se ve esa base terminada usando el ejemplo del docente; tu entregable real tiene la misma estructura, con el contenido que tú construiste en S7-S11.

## 3. Evaluación teórico-práctica (S7-S11)

Cubre los cinco temas dictados antes de esta sesión. El docente puede tomarla escrita, oral o mixta.

**Tabla 2. Temario de la evaluación teórico-práctica**

| Sesión | Tema | Qué puede evaluar el docente |
|---|---|---|
| S7 | Arquitectura Oracle e instancia | SGA, PGA, procesos *background*, `ORACLE_HOME`/`ORACLE_SID`, administración desde Oracle Linux, y por qué una conexión `SYSDBA` no es lo mismo que una conexión de aplicación. |
| S8 | Gestión de usuarios, roles y privilegios | Creación de usuarios de base de datos, roles, privilegios de sistema frente a privilegios de objeto, el principio de mínimo privilegio, consulta de privilegios con el diccionario de datos, y por qué `BOMERP_APP` no debería tener más privilegios de los que el backend realmente usa. |
| S9 | Administración del almacenamiento y seguridad | Tablespaces, segmentos, extents, datafiles, redo logs, undo, `archivelog`, la diferencia entre auditoría de sentencias y de acceso (Unified Auditing), y qué muestra Enterprise Manager Express que el diccionario de datos solo no muestra. |
| S10 | Optimización del rendimiento | Cost Based Optimizer, `EXPLAIN PLAN`, `DBMS_STATS`, verificación del uso real de los índices existentes, qué es AWR y por qué su disponibilidad depende de la licencia (Oracle Database Free frente a Enterprise Edition), las vistas dinámicas `V$` como alternativa sin licencia, y cuándo un índice compuesto se justifica frente a varios índices simples. |
| S11 | Particionamiento y escalabilidad | `Range`, `Hash`, `List` y `Composite Partitioning`, qué significa *partition pruning*, por qué envolver la clave de partición en una función lo desactiva sin ningún error de Oracle, y por qué `DROP PARTITION` puede ser mucho más rápido que un `DELETE` equivalente. |

Preguntas de referencia (el docente puede formular equivalentes):

1. ¿Por qué una conexión como `SYSDBA` no reemplaza la necesidad de un usuario de aplicación con privilegios recortados?
2. En tu propio proyecto, ¿qué privilegio le quitaste a `BOMERP_APP` (o su equivalente) después de confirmar que el backend no lo usaba?
3. ¿Qué diferencia hay entre auditar una sentencia (`INSERT`/`UPDATE`/`DELETE`) y auditar un acceso (`LOGON`/`LOGOFF`)? ¿Cuál de las dos te habría ayudado a detectar un acceso denegado de S8?
4. Si AWR no estuviera disponible en tu ambiente por licencia, ¿qué vista dinámica usarías en su lugar, y qué información pierdes al no tener AWR?
5. ¿Por qué un índice compuesto `(ESTADO, FECHA)` puede ser mejor que dos índices simples sobre esas mismas columnas, para una consulta que filtra por ambas a la vez?
6. ¿Por qué envolver la clave de partición en una función (como `TRUNC`) desactiva el *partition pruning*, y cómo se detecta eso en un `EXPLAIN PLAN` real?
7. ¿Por qué `DROP PARTITION` puede ser más de 30 veces más rápido que un `DELETE` equivalente, en términos del mecanismo real y no solo del resultado?

## 4. Sustentación de la base de datos administrada

**Tabla 3. Distribución de tiempo de la sustentación**

| Momento | Tiempo | Quién | Propósito |
|---|---:|---|---|
| Alcance del proyecto (diapositiva) | 2-3 min | Grupal | **Una sola vez para los tres cursos** (si ADS, BD2 y LP2 se sustentan juntos) — qué hace el sistema, para quién, y su relación con el Proyecto Integrador. No se repite en ADS/LP2. |
| Arquitectura (diapositiva) | 2-3 min | Grupal | Componentes reales de la instancia propia (SGA/PGA, tablespaces, el usuario de aplicación recortado) — diagrama simple: Oracle Linux → instancia → tablespaces/usuarios/roles → particiones, si aplica. |
| Asignación de tareas (diapositiva) | 2-3 min | Grupal | Asignación por objeto real: cada integrante dueño de al menos un objeto Oracle verificable en vivo (un rol, una política de auditoría, un índice justificado, o una partición). |
| Demo y sustentación | 3 min por integrante | Individual | Cada integrante ejecuta en vivo y sustenta el objeto que administró: matriz de accesos, auditoría, `EXPLAIN PLAN` o *pruning*. |
| Preguntas | 3 min por integrante | Individual | Verificar dominio y aporte propio, con base en la Tabla 2. |

**Tabla 4. Entregables obligatorios**

| Entregable | Evidencia mínima | Criterio de aceptación |
|---|---|---|
| Producto de unidad | [`bd2-producto.md`](../../proyecto-integrador/u2/bd2-producto.md) completo | Coherente con el sílabo y con los objetos Oracle reales |
| Evidencia de integración | Esquemas administrados consumidos por el backend real de LP2, con evidencia de auditoría y de *partition pruning* documentada | Trazabilidad verificable con ADS y LP2, no solo documentada |
| Sustentación individual | Preguntas y defensa por integrante (sección 3) | Autoría demostrada |

Secuencia sugerida de presentación (referencias a secciones de `bd2-producto.md`):

1. Presentar los componentes de la instancia y confirmar en vivo una conexión `SYSDBA` distinta de la de aplicación.
2. Mostrar la matriz de accesos (`DBA_SYS_PRIVS`/`DBA_TAB_PRIVS`/`DBA_ROLE_PRIVS`) y provocar un acceso denegado a propósito.
3. Disparar una auditoría de sentencia y una de acceso, mostrando el registro real en `UNIFIED_AUDIT_TRAIL`.
4. Comparar un `EXPLAIN PLAN` antes y después del índice compuesto de S10, con la selectividad que justificó la decisión.
5. Mostrar el *partition pruning* real (`Pstart = Pstop`) y el mismo plan roto al envolver la clave en una función.
6. Medir en vivo `DROP PARTITION` frente a `DELETE` sobre el mismo volumen, y justificar la decisión con los tiempos obtenidos.
7. Cerrar con la tabla de evidencia de integración con ADS y LP2 de `bd2-producto.md`, explicando al menos una fila con evidencia en vivo desde el backend de LP2.

Criterios mínimos de aceptación:

- Los objetos Oracle existen y responden en vivo, no solo en captura de pantalla.
- La matriz de accesos está verificada contra el diccionario de datos, con al menos un acceso denegado real.
- Al menos una auditoría de sentencia y una de acceso se disparan y se explican correctamente.
- La comparación de `EXPLAIN PLAN` incluye el índice o la partición que justificó la decisión, con AWR o su alternativa de vistas dinámicas documentada.
- El *partition pruning* está verificado, incluido el caso en que una función sobre la clave lo desactiva.
- La decisión `DROP PARTITION`/`DELETE` está medida con tiempos reales, no estimados.
- Cada integrante responde individualmente al menos una pregunta de la Tabla 2.

## 5. Rúbrica de evaluación

La rúbrica (6 criterios: 5 cita literal de los criterios de evaluación del producto de la Unidad II + sustentación) vive en [`bd2-producto.md`](../../proyecto-integrador/u2/bd2-producto.md#4-rubrica-de-evaluacion), junto con la plantilla del producto y su trazabilidad con la malla curricular (CE022 Nivel 2, parcial). Úsala directamente desde ahí para calificar la sustentación de esta sesión — no se duplica aquí.
